package io.github.racoondog.wabbajack.config;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.FloatRange;

public class WabbajackConfig extends WrappedConfig {
    public boolean cooldown = false;

    @FloatRange(min = 0.1f, max = Float.MAX_VALUE)
    public float cooldownSeconds = 1.0f;

    public AbstractEffectConfig attributeScrambleEffect = new AbstractEffectConfig();
    public AbstractEffectConfig disintegrationEffect = new AbstractEffectConfig();
    public AbstractEffectConfig fearEffect = new AbstractEffectConfig();
    public FireballEffectConfig fireballEffect = new FireballEffectConfig();
    public static class FireballEffectConfig implements WrappedConfig.Section {
        public boolean enabled = true;
        public int weight = 1;
        public float explosionPower = 3.0f;
    }
    public FreezeEffectConfig freezeEffect = new FreezeEffectConfig();
    public static class FreezeEffectConfig extends AbstractEffectConfig {
        public boolean enabled = true;
        public int weight = 1;
        public float areaSize = 3.0f;
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
