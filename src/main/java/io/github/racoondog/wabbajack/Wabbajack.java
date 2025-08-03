package io.github.racoondog.wabbajack;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class Wabbajack implements ModInitializer {
	public static final String MOD_ID = "wabbajack";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static WabbajackItem WABBAJACK_ITEM;
	public static Item WABBAJACK_PROJECTILE_ITEM;
	public static EntityType<WabbajackProjectileEntity> WABBAJACK_PROJECTILE;

	@Override
	public void onInitialize() {
		WABBAJACK_ITEM = register("wabbajack", WabbajackItem::new, new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
		WABBAJACK_PROJECTILE_ITEM = register("wabbajack_projectile", Item::new, new Item.Settings());

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
			.register(itemGroup -> itemGroup.addAfter(Items.MACE, WABBAJACK_ITEM.getDefaultStack()));

		DispenserBlock.registerProjectileBehavior(WABBAJACK_ITEM);

		WABBAJACK_PROJECTILE = register("wabbajack_projectile",
			EntityType.Builder.<WabbajackProjectileEntity>create(WabbajackProjectileEntity::new, SpawnGroup.MISC)
				.dropsNothing()
				.dimensions(0.5f, 0.5f)
				.eyeHeight(0.25F)
				.maxTrackingRange(4)
				.trackingTickInterval(20)
		);
	}

	private static <T extends Item> T register(String name, Function<Item.Settings, T> itemFactory, Item.Settings settings) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
		T item = itemFactory.apply(settings.registryKey(key));
		return Registry.register(Registries.ITEM, key, item);
	}

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, name));
		return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
	}
}