package io.github.racoondog.wabbajack.config;

import folk.sisby.kaleido.api.WrappedConfig;

public class AbstractEffectConfig implements WrappedConfig.Section {
    public boolean enabled = true;
    public int weight = 1;

    public float areaSize = 3.0f;
}
