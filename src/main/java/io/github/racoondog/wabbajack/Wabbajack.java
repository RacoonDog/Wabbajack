package io.github.racoondog.wabbajack;

import io.github.racoondog.wabbajack.config.WabbajackConfig;
import io.github.racoondog.wabbajack.effects.WabbajackEffect;
import io.github.racoondog.wabbajack.effects.types.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wabbajack implements ModInitializer {
	public static final String MOD_ID = "wabbajack";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final WabbajackConfig CONFIG = WabbajackConfig.createToml(
		FabricLoader.getInstance().getConfigDir(),
		"",
		MOD_ID,
		WabbajackConfig.class
	);
	private static Pool<WabbajackEffect> EFFECTS;

	@Override
	public void onInitialize() {
		ModRegistry.initialize();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
			.register(itemGroup -> itemGroup.addAfter(Items.MACE, ModRegistry.WABBAJACK_ITEM.getDefaultStack()));

		DispenserBlock.registerBehavior(ModRegistry.WABBAJACK_ITEM, new WabbajackDispenserBehavior());

		CONFIG.registerCallback(cfg -> updateEffectPool());

		updateEffectPool();
	}

	private void updateEffectPool() {
		Pool.Builder<WabbajackEffect> builder = Pool.builder();

		if (CONFIG.attributeScrambleEffect.enabled) builder.add(new AttributeScrambleEffect(), CONFIG.attributeScrambleEffect.weight);
		if (CONFIG.disintegrationEffect.enabled) builder.add(new DisintegrationEffect(), CONFIG.disintegrationEffect.weight);
		if (CONFIG.fearEffect.enabled) builder.add(new FearEffect(), CONFIG.fearEffect.weight);
		if (CONFIG.fireballEffect.enabled) builder.add(new FireballEffect(), CONFIG.fireballEffect.weight);
		if (CONFIG.freezeEffect.enabled) builder.add(new FreezeEffect(), CONFIG.freezeEffect.weight);
		if (CONFIG.frenzyEffect.enabled) builder.add(new FrenzyEffect(), CONFIG.frenzyEffect.weight);
		if (CONFIG.furyEffect.enabled) builder.add(new FuryEffect(), CONFIG.furyEffect.weight);
		if (CONFIG.healEffect.enabled) builder.add(new HealEffect(), CONFIG.healEffect.weight);
		if (CONFIG.magicMissilesEffect.enabled) builder.add(new MagicMissilesEffect(), CONFIG.magicMissilesEffect.weight);
		if (CONFIG.teleportationEffect.enabled) builder.add(new TeleportationEffect(), CONFIG.teleportationEffect.weight);
		if (CONFIG.thunderboltEffect.enabled) builder.add(new ThunderboltEffect(), CONFIG.thunderboltEffect.weight);
		if (CONFIG.transformationEffect.enabled) builder.add(new TransformationEffect(), CONFIG.transformationEffect.weight);

		EFFECTS = builder.build();
	}

	public static WabbajackEffect getEffect(World world, boolean hasCaster) {
		WabbajackEffect effect;
		int depth = 0;
		do {
			effect = EFFECTS.get(world.random);
			depth++;
		} while (effect.requiresCaster() && !hasCaster && depth < 10);

		if (depth == 10) {
			LOGGER.warn("No valid The Wabbajack! effects matching config.");
		}

		return effect;
	}
}