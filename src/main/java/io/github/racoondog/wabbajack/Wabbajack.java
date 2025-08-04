package io.github.racoondog.wabbajack;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wabbajack implements ModInitializer {
	public static final String MOD_ID = "wabbajack";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModRegistry.initialize();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
			.register(itemGroup -> itemGroup.addAfter(Items.MACE, ModRegistry.WABBAJACK_ITEM.getDefaultStack()));

		DispenserBlock.registerBehavior(ModRegistry.WABBAJACK_ITEM, new WabbajackDispenserBehavior());
	}
}