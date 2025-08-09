package io.github.racoondog.wabbajack;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public final class ModRegistry {
    public static final WabbajackItem WABBAJACK_ITEM;
    public static final Item WABBAJACK_PROJECTILE_ITEM;
    public static final EntityType<WabbajackProjectileEntity> WABBAJACK_PROJECTILE;
    public static final FrostBlock FROST_BLOCK;
    public static final RegistryKey<DamageType> DISINTEGRATED = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Wabbajack.MOD_ID, "disintegrated"));
    public static final RegistryKey<DamageType> MADNESS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Wabbajack.MOD_ID, "madness"));
    public static final RegistryKey<DamageType> DISFIGURED = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Wabbajack.MOD_ID, "disfigured"));

    static {
        Item.Settings wabbajackSettings = new Item.Settings().maxCount(1).rarity(Rarity.EPIC).maxDamage(80).repairable(Items.NETHERITE_INGOT);
        if (Wabbajack.CONFIG.cooldown) wabbajackSettings.useCooldown(1);

        WABBAJACK_ITEM = register("wabbajack", WabbajackItem::new, wabbajackSettings);
        WABBAJACK_PROJECTILE_ITEM = register("wabbajack_projectile", Item::new, new Item.Settings());

        WABBAJACK_PROJECTILE = register("wabbajack_projectile",
            EntityType.Builder.<WabbajackProjectileEntity>create(WabbajackProjectileEntity::new, SpawnGroup.MISC)
                .dropsNothing()
                .dimensions(0.5f, 0.5f)
                .eyeHeight(0.25F)
                .maxTrackingRange(4)
                .trackingTickInterval(20)
        );

        FROST_BLOCK = register("frost", FrostBlock::new, AbstractBlock.Settings.copy(Blocks.FROSTED_ICE).suffocates(Blocks::never));
    }

    private ModRegistry() {}

    public static void initialize() {}

    private static <T extends Item> T register(String name, Function<Item.Settings, T> itemFactory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Wabbajack.MOD_ID, name));
        T item = itemFactory.apply(settings.registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Wabbajack.MOD_ID, name));
        return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
    }

    private static <T extends Block> T register(String name, Function<AbstractBlock.Settings, T> blockFactory, AbstractBlock.Settings settings) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Wabbajack.MOD_ID, name));
        T block = blockFactory.apply(settings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }
}
