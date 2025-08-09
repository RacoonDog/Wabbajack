package io.github.racoondog.wabbajack;

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
	public static Pool<WabbajackEffect> EFFECTS;

	@Override
	public void onInitialize() {
		ModRegistry.initialize();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
			.register(itemGroup -> itemGroup.addAfter(Items.MACE, ModRegistry.WABBAJACK_ITEM.getDefaultStack()));

		DispenserBlock.registerBehavior(ModRegistry.WABBAJACK_ITEM, new WabbajackDispenserBehavior());

		updateEffectPool();
	}

	public static void updateEffectPool() {
		Pool.Builder<WabbajackEffect> builder = Pool.builder();

		if (CONFIG.effectToggles.attributeScramble) builder.add(new AttributeScrambleEffect(), CONFIG.effectWeights.attributeScramble);
		if (CONFIG.effectToggles.disintegration) builder.add(new DisintegrationEffect(), CONFIG.effectWeights.disintegration);
		if (CONFIG.effectToggles.fear) builder.add(new FearEffect(), CONFIG.effectWeights.fear);
		if (CONFIG.effectToggles.fireball) builder.add(new FireballEffect(), CONFIG.effectWeights.fireball);
		if (CONFIG.effectToggles.freeze) builder.add(new FreezeEffect(), CONFIG.effectWeights.freeze);
		if (CONFIG.effectToggles.frenzy) builder.add(new FrenzyEffect(), CONFIG.effectWeights.frenzy);
		if (CONFIG.effectToggles.fury) builder.add(new FuryEffect(), CONFIG.effectWeights.fury);
		if (CONFIG.effectToggles.heal) builder.add(new HealEffect(), CONFIG.effectWeights.heal);
		if (CONFIG.effectToggles.magicMissiles) builder.add(new MagicMissilesEffect(), CONFIG.effectWeights.magicMissiles);
		if (CONFIG.effectToggles.teleportation) builder.add(new TeleportationEffect(), CONFIG.effectWeights.teleportation);
		if (CONFIG.effectToggles.thunderbolt) builder.add(new ThunderboltEffect(), CONFIG.effectWeights.thunderbolt);
		if (CONFIG.effectToggles.transformation) builder.add(new TransformationEffect(), CONFIG.effectWeights.transformation);

		EFFECTS = builder.build();
	}

	public static WabbajackEffect getEffect(World world, boolean hasCaster) {
		if (hasCaster) {
			return EFFECTS.get(world.random);
		} else {
			int maxDepth = Math.max(10, EFFECTS.getEntries().size() * 2);

			WabbajackEffect effect;
			int depth = 0;
			do {
				effect = EFFECTS.get(world.random);
				depth++;
			} while (effect.requiresCaster() && depth < 10);

			if (depth == maxDepth) {
				LOGGER.warn("No valid The Wabbajack! effects matching config.");
			}

			return effect;
		}
	}
}