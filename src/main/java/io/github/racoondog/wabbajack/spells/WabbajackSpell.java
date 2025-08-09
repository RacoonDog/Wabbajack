package io.github.racoondog.wabbajack.spells;

import io.github.racoondog.wabbajack.ModRegistry;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import org.jetbrains.annotations.Nullable;

public abstract class WabbajackSpell {
    public void onItemUse(ServerWorld world, PlayerEntity user, ItemStack stack) {
        ProjectileItem.Settings projectileSettings = ModRegistry.WABBAJACK_ITEM.getProjectileSettings();

        WabbajackProjectileEntity entity = ProjectileEntity.spawnWithVelocity(WabbajackProjectileEntity::new, world, stack, user, 0.0F, projectileSettings.power(), projectileSettings.uncertainty());
        entity.spell = this;

        world.playSoundFromEntity(null, user, SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.PLAYERS, 1.0F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
    }

    public void onDispense(ServerWorld world, Direction direction, Position position, ItemStack stack) {
        ProjectileItem.Settings projectileSettings = ModRegistry.WABBAJACK_ITEM.getProjectileSettings();

        WabbajackProjectileEntity entity = (WabbajackProjectileEntity) ProjectileEntity.spawnWithVelocity(
            ModRegistry.WABBAJACK_ITEM.createEntity(world, position, stack, direction),
            world,
            stack,
            direction.getOffsetX(),
            direction.getOffsetY(),
            direction.getOffsetZ(),
            projectileSettings.power(),
            projectileSettings.uncertainty()
        );

        entity.spell = this;
    }

    public boolean requiresCaster() {
        return false;
    }

    public void onProjectileCollision(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, @Nullable LivingEntity caster) {}

    protected static void discard(ServerWorld world, Entity entity, @Nullable RegistryKey<DamageType> damageType) {
        if (entity instanceof PlayerEntity || entity instanceof Tameable tameable && tameable.getOwnerReference() != null) {
            entity.damage(world, world.getDamageSources().create(damageType != null ? damageType : ModRegistry.MADNESS), Float.MAX_VALUE);
        } else {
            entity.discard();
        }
    }
}
