package io.github.racoondog.wabbajack.config;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.FloatRange;

public class WabbajackConfig extends WrappedConfig {
    public boolean cooldown = false;

    @FloatRange(min = 0.1f, max = Float.MAX_VALUE)
    public float cooldownSeconds = 1.0f;
}
