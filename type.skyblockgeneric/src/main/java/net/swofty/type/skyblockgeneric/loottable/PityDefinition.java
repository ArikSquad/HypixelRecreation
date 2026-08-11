package net.swofty.type.skyblockgeneric.loottable;

public record PityDefinition(String id, long threshold) {
    public PityDefinition {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Pity id cannot be blank");
        if (threshold <= 0) throw new IllegalArgumentException("Pity threshold must be positive");
    }
}
