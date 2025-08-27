package io.github.racoondog.wabbajack.impl.compat.losing_my_marbles;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.Config;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Processor;
import io.github.racoondog.wabbajack.impl.Wabbajack;

@Processor("processor")
public class MarbleTossConfig extends WrappedConfig {
    @SuppressWarnings("unused")
    public void processor(Config.Builder builder) {
        builder.callback(config -> {
            if (Wabbajack.CONFIG != null) {
                Wabbajack.updateSpellPool();
            }
        });
    }

    @Comment("Whether the Marble Toss spell should be enabled")
    public boolean enabled;
    @Comment("Controls the probability of the Marble Toss spell being thrown")
    public int weight = 1;
    @Comment("How many marbles to throw per spell cast")
    public int marbles = 3;
}
