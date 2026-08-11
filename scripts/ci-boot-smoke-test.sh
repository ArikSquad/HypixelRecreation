#!/bin/sh
set -eu

unset CDPATH
REPO_ROOT=$(cd -- "$(dirname -- "$0")/.." && pwd)
cd "$REPO_ROOT"

LOG_DIR=${SMOKE_LOG_DIR:-"$REPO_ROOT/smoke-logs"}
PROXY_DIR=${SMOKE_PROXY_DIR:-"$REPO_ROOT/server/proxy"}
JAR_DIR=${SMOKE_JAR_DIR:-}
MONGODB_URI=${SMOKE_MONGODB_URI:-mongodb://localhost:27017}
REDIS_URI=${SMOKE_REDIS_URI:-redis://localhost:6379}
LIMBO_HOST=${SMOKE_LIMBO_HOST:-127.0.0.1}
LIMBO_PORT=${SMOKE_LIMBO_PORT:-65535}
FORWARDING_SECRET=${SMOKE_FORWARDING_SECRET:-ixmSUgWOgvs7}
PACK_SERVER_URL=${SMOKE_PACK_SERVER_URL:-http://127.0.0.1:7270}
API_PORT=${SMOKE_API_PORT:-8081}
VELOCITY_JAR_URL=${SMOKE_VELOCITY_JAR_URL:-https://fill-data.papermc.io/v1/objects/0ec616020166465dacca3b790d3db2b246f8f7c13b3aaacaae60c825744a66e0/velocity-3.5.0-SNAPSHOT-605.jar}
BATCH_SIZE=${SMOKE_BATCH_SIZE:-5}
PROXY_TIMEOUT=${SMOKE_PROXY_TIMEOUT:-120}
PROXY_SETTLE=${SMOKE_PROXY_SETTLE:-15}
SERVICE_TIMEOUT=${SMOKE_SERVICE_TIMEOUT:-120}
SERVICE_STAGGER=${SMOKE_SERVICE_STAGGER:-3}
SERVER_TIMEOUT=${SMOKE_SERVER_TIMEOUT:-120}
SERVER_STAGGER=${SMOKE_SERVER_STAGGER:-6}
PROXY_XMX=${SMOKE_PROXY_XMX:-512m}
SERVICE_XMX=${SMOKE_SERVICE_XMX:-512m}
SERVER_XMX=${SMOKE_SERVER_XMX:-1g}
SERVER_TYPES=${SMOKE_SERVER_TYPES:-}
LOG_TAIL=${SMOKE_LOG_TAIL:-60}

SERVER_READY='Received server name'
SERVICE_READY='initialized!'
PROXY_READY='Done ('
PROXY_PLUGIN_READY='Loaded plugin skyblock'
FATAL_PATTERN='Exception in thread|ExceptionInInitializerError|NoClassDefFoundError|NoSuchMethodError|OutOfMemoryError|Couldn.t connect to proxy|No TypeLoader found|Error occurred during initialization of VM|Could not create the Java Virtual Machine|A fatal error has been detected by the Java Runtime Environment|Unable to load plugin|Failed to load plugin'
IGNORED_PATTERN='is not online!'

PROXY_PID=
SERVICE_PIDS=
SERVICE_ENTRIES=
BATCH_PIDS=
CORE_JAR=
PROXY_PLUGIN_JAR=
SERVICE_JARS=
FAILURES=0
BOOTED=0

log() {
	printf '[smoke] %s\n' "$*"
}

group_start() {
	if [ -n "${GITHUB_ACTIONS:-}" ]; then
		printf '::group::%s\n' "$*"
	else
		log "$*"
	fi
}

group_end() {
	if [ -n "${GITHUB_ACTIONS:-}" ]; then
		printf '::endgroup::\n'
	fi
}

report_failure() {
	FAILURES=$((FAILURES + 1))
	if [ -n "${GITHUB_ACTIONS:-}" ]; then
		printf '::error::%s\n' "$1"
	fi
	log "FAILED: $1"
	if [ -n "${2:-}" ] && [ -f "$2" ]; then
		log "--- last $LOG_TAIL lines of $(basename "$2") ---"
		tail -n "$LOG_TAIL" "$2"
		log "--- end of $(basename "$2") ---"
	fi
}

kill_pid() {
	pid=$1
	if [ -z "$pid" ] || ! kill -0 "$pid" 2>/dev/null; then
		return 0
	fi
	kill -TERM "$pid" 2>/dev/null || true
	waited=0
	while [ "$waited" -lt 10 ]; do
		if ! kill -0 "$pid" 2>/dev/null; then
			wait "$pid" 2>/dev/null || true
			return 0
		fi
		sleep 1
		waited=$((waited + 1))
	done
	kill -KILL "$pid" 2>/dev/null || true
	wait "$pid" 2>/dev/null || true
}

cleanup() {
	for pid in $BATCH_PIDS $SERVICE_PIDS; do
		kill_pid "$pid"
	done
	kill_pid "$PROXY_PID"
}

has_fatal() {
	[ -f "$1" ] || return 1
	grep -Ev "$IGNORED_PATTERN" "$1" | grep -Eq "$FATAL_PATTERN"
}

fatal_excerpt() {
	grep -Ev "$IGNORED_PATTERN" "$1" | grep -E "$FATAL_PATTERN" | head -n 1
}

wait_for_ready() {
	name=$1
	file=$2
	pid=$3
	needle=$4
	timeout=$5
	waited=0
	while :; do
		if [ -f "$file" ] && grep -qF "$needle" "$file"; then
			return 0
		fi
		if has_fatal "$file"; then
			report_failure "$name hit a fatal error while booting: $(fatal_excerpt "$file")" "$file"
			return 1
		fi
		if ! kill -0 "$pid" 2>/dev/null; then
			report_failure "$name exited before it finished booting" "$file"
			return 1
		fi
		if [ "$waited" -ge "$timeout" ]; then
			report_failure "$name never logged '$needle' within ${timeout}s" "$file"
			return 1
		fi
		sleep 2
		waited=$((waited + 2))
	done
}

wait_for_uri() {
	uri=$1
	default_port=$2
	label=$3
	command -v nc >/dev/null 2>&1 || return 0
	authority=$(printf '%s' "$uri" | sed 's|^[a-zA-Z0-9+.-]*://||; s|/.*$||; s|^.*@||')
	host=${authority%%:*}
	port=${authority##*:}
	if [ "$port" = "$authority" ] || [ -z "$port" ]; then
		port=$default_port
	fi
	[ -n "$host" ] || return 0
	waited=0
	while [ "$waited" -lt 60 ]; do
		if nc -z "$host" "$port" 2>/dev/null; then
			return 0
		fi
		sleep 2
		waited=$((waited + 2))
	done
	log "warning: $label at $host:$port never accepted a connection"
}

write_config() {
	target=$1
	mkdir -p "$(dirname "$target")"
	cat > "$target" <<EOF
host-name: 0.0.0.0
mongodb: $MONGODB_URI
redis-uri: $REDIS_URI
velocity-secret: $FORWARDING_SECRET
require-auth: false
integrations:
  spark: false
  anticheat: false
  via-version: false
  sentry-dsn: ''
limbo:
  host-name: $LIMBO_HOST
  port: $LIMBO_PORT
resource-packs:
  skyblockpack:
    server-url: $PACK_SERVER_URL
  testingpack:
    server-url: $PACK_SERVER_URL
  ravengard:
    server-url: $PACK_SERVER_URL
EOF
}

resolve_jars() {
	if [ -n "$JAR_DIR" ]; then
		CORE_JAR=${SMOKE_CORE_JAR:-"$JAR_DIR/HypixelCore.jar"}
		PROXY_PLUGIN_JAR=${SMOKE_PROXY_PLUGIN_JAR:-"$JAR_DIR/SkyBlockProxy.jar"}
		SERVICE_JARS=$(find "$JAR_DIR" -maxdepth 1 -name 'Service*.jar' | sort)
	else
		CORE_JAR=${SMOKE_CORE_JAR:-"$REPO_ROOT/loader/build/libs/HypixelCore.jar"}
		PROXY_PLUGIN_JAR=${SMOKE_PROXY_PLUGIN_JAR:-"$REPO_ROOT/velocity.extension/build/libs/SkyBlockProxy.jar"}
		SERVICE_JARS=$(find "$REPO_ROOT" -maxdepth 4 -path '*/service.*/build/libs/Service*.jar' | sort)
	fi

	for jar in "$CORE_JAR" "$PROXY_PLUGIN_JAR"; do
		if [ ! -f "$jar" ]; then
			log "missing jar: $jar (run ./gradlew shadowJar first)"
			exit 1
		fi
	done
	if [ -z "$SERVICE_JARS" ]; then
		log "no Service*.jar found (run ./gradlew shadowJar first)"
		exit 1
	fi
}

declared_server_types() {
	sed -n 's/^[[:space:]]*\([A-Z][A-Z0-9_]*\)(\(true\|false\)).*/\1/p' \
		commons/src/main/java/net/swofty/commons/ServerType.java
}

resolve_server_types() {
	if [ -n "$SERVER_TYPES" ]; then
		return 0
	fi
	SERVER_TYPES=$(declared_server_types | tr '\n' ' ')
	SERVER_TYPES=${SERVER_TYPES% }
	if [ -z "$SERVER_TYPES" ]; then
		log "could not resolve any server types from ServerType.java"
		exit 1
	fi
}

start_proxy() {
	mkdir -p "$PROXY_DIR/plugins"
	if [ ! -f "$PROXY_DIR/velocity.jar" ]; then
		log "downloading velocity from $VELOCITY_JAR_URL"
		curl -fSL --retry 3 -o "$PROXY_DIR/velocity.jar" "$VELOCITY_JAR_URL"
	fi
	cp "$PROXY_PLUGIN_JAR" "$PROXY_DIR/plugins/SkyBlockProxy.jar"
	cp "$REPO_ROOT/configuration/velocity.toml" "$PROXY_DIR/velocity.toml"
	printf '%s' "$FORWARDING_SECRET" > "$PROXY_DIR/forwarding.secret"
	write_config "$PROXY_DIR/configuration/config.yml"

	log "starting proxy from $PROXY_DIR"
	(
		cd "$PROXY_DIR"
		exec java "-Xmx$PROXY_XMX" -jar velocity.jar
	) > "$LOG_DIR/proxy.log" 2>&1 < /dev/null &
	PROXY_PID=$!

	wait_for_ready "proxy" "$LOG_DIR/proxy.log" "$PROXY_PID" "$PROXY_READY" "$PROXY_TIMEOUT" || return 1
	if ! grep -qF "$PROXY_PLUGIN_READY" "$LOG_DIR/proxy.log"; then
		report_failure "proxy came up without loading the SkyBlock plugin" "$LOG_DIR/proxy.log"
		return 1
	fi
	log "proxy is up, waiting ${PROXY_SETTLE}s before starting backends"
	sleep "$PROXY_SETTLE"
}

start_services() {
	group_start "Starting services"
	for jar in $SERVICE_JARS; do
		name=$(basename "$jar" .jar)
		lower=$(printf '%s' "$name" | tr '[:upper:]' '[:lower:]')
		logfile="$LOG_DIR/service-$lower.log"
		if [ "$name" = "ServiceAPI" ]; then
			java "-Xmx$SERVICE_XMX" -jar "$jar" "--port=$API_PORT" > "$logfile" 2>&1 < /dev/null &
		else
			java "-Xmx$SERVICE_XMX" -jar "$jar" > "$logfile" 2>&1 < /dev/null &
		fi
		pid=$!
		SERVICE_PIDS="$SERVICE_PIDS $pid"
		SERVICE_ENTRIES="$SERVICE_ENTRIES $name:$pid:$logfile"
		log "launched $name (pid $pid)"
		sleep "$SERVICE_STAGGER"
	done

	for entry in $SERVICE_ENTRIES; do
		name=${entry%%:*}
		rest=${entry#*:}
		pid=${rest%%:*}
		logfile=${rest#*:}
		if wait_for_ready "$name" "$logfile" "$pid" "$SERVICE_READY" "$SERVICE_TIMEOUT"; then
			log "$name is up"
		fi
	done
	group_end
}

check_services_alive() {
	for entry in $SERVICE_ENTRIES; do
		name=${entry%%:*}
		rest=${entry#*:}
		pid=${rest%%:*}
		logfile=${rest#*:}
		if ! kill -0 "$pid" 2>/dev/null; then
			report_failure "$name died before the run finished" "$logfile"
		fi
	done
}

run_batch() {
	batch=$1
	index=$2
	total=$3
	entries=
	ready=
	BATCH_PIDS=
	group_start "Batch $index/$total:$batch"
	for type in $batch; do
		lower=$(printf '%s' "$type" | tr '[:upper:]' '[:lower:]')
		logfile="$LOG_DIR/server-$lower.log"
		java "-Xmx$SERVER_XMX" -jar "$CORE_JAR" "$type" > "$logfile" 2>&1 < /dev/null &
		pid=$!
		entries="$entries $type:$pid:$logfile"
		BATCH_PIDS="$BATCH_PIDS $pid"
		log "launched $type (pid $pid)"
		sleep "$SERVER_STAGGER"
	done

	for entry in $entries; do
		type=${entry%%:*}
		rest=${entry#*:}
		pid=${rest%%:*}
		logfile=${rest#*:}
		if wait_for_ready "$type" "$logfile" "$pid" "$SERVER_READY" "$SERVER_TIMEOUT"; then
			BOOTED=$((BOOTED + 1))
			ready="$ready $entry"
			log "$type registered with the proxy"
		fi
	done

	for entry in $ready; do
		type=${entry%%:*}
		rest=${entry#*:}
		logfile=${rest#*:}
		if has_fatal "$logfile"; then
			report_failure "$type registered but then hit a fatal error: $(fatal_excerpt "$logfile")" "$logfile"
		fi
	done

	for entry in $entries; do
		rest=${entry#*:}
		pid=${rest%%:*}
		kill_pid "$pid"
	done
	BATCH_PIDS=
	group_end

	if ! kill -0 "$PROXY_PID" 2>/dev/null; then
		report_failure "proxy died during batch $index" "$LOG_DIR/proxy.log"
		return 1
	fi
}

main() {
	rm -rf "$LOG_DIR"
	mkdir -p "$LOG_DIR"

	resolve_jars
	resolve_server_types
	write_config "$REPO_ROOT/configuration/config.yml"

	wait_for_uri "$MONGODB_URI" 27017 "mongodb"
	wait_for_uri "$REDIS_URI" 6379 "redis"

	trap cleanup EXIT INT TERM

	if ! start_proxy; then
		log "proxy never became healthy, aborting"
		exit 1
	fi

	start_services

	total_types=$(printf '%s' "$SERVER_TYPES" | wc -w | tr -d ' ')
	total_batches=$(((total_types + BATCH_SIZE - 1) / BATCH_SIZE))
	log "booting $total_types server types in $total_batches batches of up to $BATCH_SIZE"

	batch=
	count=0
	index=0
	for type in $SERVER_TYPES; do
		batch="$batch $type"
		count=$((count + 1))
		if [ "$count" -eq "$BATCH_SIZE" ]; then
			index=$((index + 1))
			run_batch "$batch" "$index" "$total_batches" || exit 1
			batch=
			count=0
		fi
	done
	if [ -n "$batch" ]; then
		index=$((index + 1))
		run_batch "$batch" "$index" "$total_batches" || exit 1
	fi

	check_services_alive

	log "$BOOTED of $total_types server types booted cleanly"
	if [ "$FAILURES" -gt 0 ]; then
		log "$FAILURES failure(s) detected"
		exit 1
	fi
	log "full stack boot smoke test passed"
}

main "$@"
