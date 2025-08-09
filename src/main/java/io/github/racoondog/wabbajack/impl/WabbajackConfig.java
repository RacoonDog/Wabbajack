package io.github.racoondog.wabbajack.impl;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.Config;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.DisplayName;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.FloatRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.IntegerRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Processor;

@Processor("processor")
public class WabbajackConfig extends WrappedConfig {
    public void processor(Config.Builder builder) {
        builder.callback(config -> {
            if (Wabbajack.CONFIG != null) {
                Wabbajack.updateSpellPool();
            }
        });
    }

    public boolean cooldown = false;

    @FloatRange(min = 0.1f, max = 10f)
    public float cooldownSeconds = 1.0f;

    @DisplayName("Area of Effect Size")
    @FloatRange(min = 0.1f, max = 100f)
    public float aoeSize = 3.0f;

    public SpellToggles spellToggles = new SpellToggles();
    public static class SpellToggles implements WrappedConfig.Section {
        public boolean attributeScramble = true;
        public boolean disintegration = true;
        public boolean fear = true;
        public boolean fireball = true;
        public boolean freeze = true;
        public boolean frenzy = true;
        public boolean fury = true;
        public boolean heal = true;
        public boolean magicMissiles = true;
        public boolean teleportation = true;
        public boolean thunderbolt = true;
        public boolean transformation = true;
    }

    public SpellWeights spellWeights = new SpellWeights();
    public static class SpellWeights implements WrappedConfig.Section {
        @IntegerRange(min = 1, max = 100)
        public int attributeScramble = 1;
        @IntegerRange(min = 1, max = 100)
        public int disintegration = 1;
        @IntegerRange(min = 1, max = 100)
        public int fear = 1;
        @IntegerRange(min = 1, max = 100)
        public int fireball = 1;
        @IntegerRange(min = 1, max = 100)
        public int freeze = 1;
        @IntegerRange(min = 1, max = 100)
        public int frenzy = 1;
        @IntegerRange(min = 1, max = 100)
        public int fury = 1;
        @IntegerRange(min = 1, max = 100)
        public int heal = 1;
        @IntegerRange(min = 1, max = 100)
        public int magicMissiles = 1;
        @IntegerRange(min = 1, max = 100)
        public int teleportation = 1;
        @IntegerRange(min = 1, max = 100)
        public int thunderbolt = 1;
        @IntegerRange(min = 1, max = 100)
        public int transformation = 1;
    }

    public AttributeScrambleSpellConfig attributeScrambleSpell = new AttributeScrambleSpellConfig();
    public static class AttributeScrambleSpellConfig implements WrappedConfig.Section {
        public int attributes = 7;
        public double magnitude = 2.0d;
    }

    public FireballSpellConfig fireballSpell = new FireballSpellConfig();
    public static class FireballSpellConfig implements WrappedConfig.Section {
        public float explosionPower = 3.0f;
    }

    public FreezeSpellConfig freezeSpell = new FreezeSpellConfig();
    public static class FreezeSpellConfig implements WrappedConfig.Section {
        public boolean placesBlocks = true;
    }

    public MagicMissilesSpellConfig magicMissilesSpell = new MagicMissilesSpellConfig();
    public static class MagicMissilesSpellConfig implements WrappedConfig.Section {
        public float damage = 5.0f;
        public int projectiles = 5;
    }
}
