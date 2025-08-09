package io.github.racoondog.wabbajack;

import io.github.racoondog.wabbajack.spells.WabbajackSpell;
import io.github.racoondog.wabbajack.spells.types.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
	public static Pool<WabbajackSpell> SPELLS;

	@Override
	public void onInitialize() {
		ModRegistry.initialize();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
			.register(itemGroup -> itemGroup.addAfter(Items.MACE, ModRegistry.WABBAJACK_ITEM.getDefaultStack()));

		DispenserBlock.registerBehavior(ModRegistry.WABBAJACK_ITEM, new WabbajackDispenserBehavior());

		ServerTickEvents.START_WORLD_TICK.register(MagicMissilesSpell::tick);

		updateSpellPool();
	}

	public static void updateSpellPool() {
		Pool.Builder<WabbajackSpell> builder = Pool.builder();

		if (CONFIG.spellToggles.attributeScramble) builder.add(new AttributeScrambleSpell(), CONFIG.spellWeights.attributeScramble);
		if (CONFIG.spellToggles.disintegration) builder.add(new DisintegrationSpell(), CONFIG.spellWeights.disintegration);
		if (CONFIG.spellToggles.fear) builder.add(new FearSpell(), CONFIG.spellWeights.fear);
		if (CONFIG.spellToggles.fireball) builder.add(new FireballSpell(), CONFIG.spellWeights.fireball);
		if (CONFIG.spellToggles.freeze) builder.add(new FreezeSpell(), CONFIG.spellWeights.freeze);
		if (CONFIG.spellToggles.frenzy) builder.add(new FrenzySpell(), CONFIG.spellWeights.frenzy);
		if (CONFIG.spellToggles.fury) builder.add(new FurySpell(), CONFIG.spellWeights.fury);
		if (CONFIG.spellToggles.heal) builder.add(new HealSpell(), CONFIG.spellWeights.heal);
		if (CONFIG.spellToggles.magicMissiles) builder.add(new MagicMissilesSpell(), CONFIG.spellWeights.magicMissiles);
		if (CONFIG.spellToggles.teleportation) builder.add(new TeleportationSpell(), CONFIG.spellWeights.teleportation);
		if (CONFIG.spellToggles.thunderbolt) builder.add(new ThunderboltSpell(), CONFIG.spellWeights.thunderbolt);
		if (CONFIG.spellToggles.transformation) builder.add(new TransformationSpell(), CONFIG.spellWeights.transformation);

		SPELLS = builder.build();
	}

	public static WabbajackSpell getSpell(World world, boolean hasCaster) {
		if (hasCaster) {
			return SPELLS.get(world.random);
		} else {
			int maxDepth = Math.max(10, SPELLS.getEntries().size() * 2);

			WabbajackSpell spell;
			int depth = 0;
			do {
				spell = SPELLS.get(world.random);
				depth++;
			} while (spell.requiresCaster() && depth < 10);

			if (depth == maxDepth) {
				LOGGER.warn("No valid The Wabbajack! spells matching config.");
			}

			return spell;
		}
	}
}