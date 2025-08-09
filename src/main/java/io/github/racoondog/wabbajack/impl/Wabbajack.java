package io.github.racoondog.wabbajack.impl;

import io.github.racoondog.wabbajack.api.spell.WabbajackSpell;
import io.github.racoondog.wabbajack.impl.spells.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Wabbajack implements ModInitializer {
	public static final String MOD_ID = "wabbajack";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final WabbajackConfig CONFIG = WabbajackConfig.createToml(
		FabricLoader.getInstance().getConfigDir(),
		"",
		MOD_ID,
		WabbajackConfig.class
	);
	public static final List<WabbajackSpell> SPELL_REGISTRY = new ObjectArrayList<>(List.of(
		new AttributeScrambleSpell(), new ConfettiSpell(), new DisintegrationSpell(), new FearSpell(),
		new FireballSpell(), new FreezeSpell(), new FrenzySpell(), new FurySpell(), new HealSpell(),
		new MagicMissilesSpell(), new TeleportationSpell(), new ThunderboltSpell(), new TransformationSpell()
	));
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

		for (WabbajackSpell spell : SPELL_REGISTRY) {
			if (spell.isEnabled()) {
				builder.add(spell, spell.getWeight());
			}
		}

		SPELLS = builder.build();
	}

	public static @Nullable WabbajackSpell getSpell(Random random, boolean hasCaster) {
		if (SPELLS.isEmpty()) {
			return null;
		}

		if (hasCaster) {
			return SPELLS.get(random);
		} else {
			int maxDepth = Math.max(10, SPELLS.getEntries().size() * 2);

			WabbajackSpell spell;
			int depth = 0;
			do {
				spell = SPELLS.get(random);
				depth++;
			} while (spell.requiresCaster() && depth < 10);

			if (depth == maxDepth) {
				return null;
			}

			return spell;
		}
	}
}