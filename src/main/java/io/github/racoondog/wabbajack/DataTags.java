package io.github.racoondog.wabbajack;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class DataTags {
    public static final TagKey<EntityType<?>> CAN_BE_WABBAJACKED = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Wabbajack.MOD_ID, "can_be_wabbajacked"));
    public static final TagKey<EntityType<?>> CAN_TRANSFORM_INTO = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Wabbajack.MOD_ID, "can_transform_into"));
}
