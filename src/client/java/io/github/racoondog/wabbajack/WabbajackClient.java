package io.github.racoondog.wabbajack;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class WabbajackClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(Wabbajack.WABBAJACK_PROJECTILE, FlyingItemEntityRenderer::new);
	}
}