package io.github.racoondog.wabbajack;

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
                Wabbajack.updateEffectPool();
            }
        });
    }

    public boolean cooldown = false;

    @FloatRange(min = 0.1f, max = 10f)
    public float cooldownSeconds = 1.0f;

    @DisplayName("Area of Effect Size")
    @FloatRange(min = 0.1f, max = 100f)
    public float aoeSize = 3.0f;

    public EffectToggles effectToggles = new EffectToggles();
    public static class EffectToggles implements WrappedConfig.Section {
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

    public EffectWeights effectWeights = new EffectWeights();
    public static class EffectWeights implements WrappedConfig.Section {
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

    public AttributeScrambleEffectConfig attributeScrambleEffect = new AttributeScrambleEffectConfig();
    public static class AttributeScrambleEffectConfig implements WrappedConfig.Section {
        public int attributes = 7;
        public double magnitude = 2.0d;
    }

    public FireballEffectConfig fireballEffect = new FireballEffectConfig();
    public static class FireballEffectConfig implements WrappedConfig.Section {
        public float explosionPower = 3.0f;
    }

    public FreezeEffectConfig freezeEffect = new FreezeEffectConfig();
    public static class FreezeEffectConfig implements WrappedConfig.Section {
        public boolean placesBlocks = true;
    }
}
