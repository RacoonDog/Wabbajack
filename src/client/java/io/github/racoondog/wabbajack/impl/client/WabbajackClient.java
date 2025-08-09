package io.github.racoondog.wabbajack.impl.client;

import io.github.racoondog.wabbajack.impl.ModRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class WabbajackClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModRegistry.WABBAJACK_PROJECTILE, FlyingItemEntityRenderer::new);
		EntityRendererRegistry.register(ModRegistry.MAGIC_MISSILE_PROJECTILE, FlyingItemEntityRenderer::new);
		BlockRenderLayerMap.putBlock(ModRegistry.FROST_BLOCK, BlockRenderLayer.TRANSLUCENT);
	}
}