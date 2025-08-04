package io.github.racoondog.wabbajack;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public final class ModRegistry {
    public final static WabbajackItem WABBAJACK_ITEM;
    public final static Item WABBAJACK_PROJECTILE_ITEM;
    public final static EntityType<WabbajackProjectileEntity> WABBAJACK_PROJECTILE;

    static {
        Item.Settings wabbajackSettings = new Item.Settings().maxCount(1).rarity(Rarity.EPIC);
        if (Wabbajack.CONFIG.cooldown) wabbajackSettings.useCooldown(Wabbajack.CONFIG.cooldownSeconds);

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
}
