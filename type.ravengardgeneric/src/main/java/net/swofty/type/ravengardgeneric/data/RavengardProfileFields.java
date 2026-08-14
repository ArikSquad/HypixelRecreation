package net.swofty.type.ravengardgeneric.data;

import net.swofty.PlayerField;
import net.swofty.codec.Codecs;
import net.swofty.commons.protocol.JacksonSerializer;
import net.swofty.commons.protocol.Serializer;

import java.util.List;

public final class RavengardProfileFields {
    public static final String NAMESPACE = "ravengard";
    public static final String LEGACY_NAMESPACE = "game";

    public static final Serializer<String> STRING = new JacksonSerializer<>(String.class);
    public static final Serializer<Integer> INTEGER = new JacksonSerializer<>(Integer.class);
    public static final Serializer<Boolean> BOOLEAN = new JacksonSerializer<>(Boolean.class);
    public static final Serializer<Long> LONG = new JacksonSerializer<>(Long.class);
    public static final Serializer<String[]> STRING_ARRAY = new JacksonSerializer<>(String[].class);

    public static final PlayerField<String> PROFILES_INDEX = field("_profiles_index");

    public static final PlayerField<String> OWNER = field("_owner");
    public static final PlayerField<String> CREATED = field("created");
    public static final PlayerField<String> CLASS = field("class");
    public static final PlayerField<String> LEVEL = field("level");
    public static final PlayerField<String> EXPERIENCE = field("experience");
    public static final PlayerField<String> CROWNS = field("crowns");
    public static final PlayerField<String> ABILITY_POINTS = field("ability_points");
    public static final PlayerField<String> TUTORIAL = field("tutorial");
    public static final PlayerField<String> PLAYTIME_SECONDS = field("playtime_seconds");
    public static final PlayerField<String> INTROS = field("intros");
    public static final PlayerField<String> INVENTORY = field("inventory");

    public static final List<PlayerField<String>> PROFILE_FIELDS = List.of(
            OWNER, CREATED, CLASS, LEVEL, EXPERIENCE, CROWNS,
            ABILITY_POINTS, TUTORIAL, PLAYTIME_SECONDS, INTROS, INVENTORY);

    public static final PlayerField<String> LEGACY_SELECTED_PROFILE = legacyField("ravengard_selected_profile");
    public static final PlayerField<String> LEGACY_CLASS = legacyField("ravengard_class");
    public static final PlayerField<String> LEGACY_LEVEL = legacyField("ravengard_level");
    public static final PlayerField<String> LEGACY_TUTORIAL = legacyField("ravengard_is_tutorial");
    public static final PlayerField<String> LEGACY_CROWNS = legacyField("ravengard_crowns");

    private RavengardProfileFields() {
    }

    private static PlayerField<String> field(String key) {
        return PlayerField.create(NAMESPACE, key, Codecs.STRING, null);
    }

    private static PlayerField<String> legacyField(String key) {
        return PlayerField.create(LEGACY_NAMESPACE, key, Codecs.STRING, null);
    }
}
