package io.github.racoondog.wabbajack.config;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.Config;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.DisplayName;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.FloatRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Processor;
import io.github.racoondog.wabbajack.Wabbajack;

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

    @DisplayName("Area of Effect Size")
    @FloatRange(min = 0.1f, max = Float.MAX_VALUE)
    public float aoeSize = 3.0f;

    public AttributeScrambleEffectConfig attributeScrambleEffect = new AttributeScrambleEffectConfig();
    public static class AttributeScrambleEffectConfig implements WrappedConfig.Section {
        public boolean enabled = true;
        public int weight = 1;
        public int attributes = 7;
        public double magnitude = 3.0d;
    }
    public AbstractEffectConfig disintegrationEffect = new AbstractEffectConfig();
    public AbstractEffectConfig fearEffect = new AbstractEffectConfig();
    public FireballEffectConfig fireballEffect = new FireballEffectConfig();
    public static class FireballEffectConfig implements WrappedConfig.Section {
        public boolean enabled = true;
        public int weight = 1;
        public float explosionPower = 3.0f;
    }
    public FreezeEffectConfig freezeEffect = new FreezeEffectConfig();
    public static class FreezeEffectConfig implements WrappedConfig.Section {
        public boolean enabled = true;
        public int weight = 1;
        public boolean placesBlocks = true;
    }
    public AbstractEffectConfig frenzyEffect = new AbstractEffectConfig();
    public AbstractEffectConfig furyEffect = new AbstractEffectConfig();
    public AbstractEffectConfig healEffect = new AbstractEffectConfig();
    public MagicMissilesEffect magicMissilesEffect = new MagicMissilesEffect();
    public static class MagicMissilesEffect implements WrappedConfig.Section {
        public boolean enabled = true;
        public int weight = 1;
        public int missiles = 3;
        public float damage = 5.0f;
    }
    public AbstractEffectConfig teleportationEffect = new AbstractEffectConfig();
    public AbstractEffectConfig thunderboltEffect = new AbstractEffectConfig();
    public AbstractEffectConfig transformationEffect = new AbstractEffectConfig();
}
