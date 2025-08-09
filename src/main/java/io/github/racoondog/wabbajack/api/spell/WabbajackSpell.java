package io.github.racoondog.wabbajack.api.spell;

import io.github.racoondog.wabbajack.impl.ModRegistry;
import io.github.racoondog.wabbajack.impl.WabbajackProjectileEntity;
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

/**
 * All Wabbajack spells have to extend this class. Spells which use their own {@link ProjectileEntity} or any such
 * custom behaviour (for example, {@link io.github.racoondog.wabbajack.impl.spells.MagicMissilesSpell}) should override
 * {@link WabbajackSpell#onItemUse(ServerWorld, PlayerEntity, ItemStack)} and
 * {@link WabbajackSpell#onDispense(ServerWorld, Direction, Position, ItemStack)}.
 */
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

    /**
     * @return whether this spell requires a caster entity. Spells that require a caster cannot be summoned or thrown
     * from a dispenser.
     */
    public boolean requiresCaster() {
        return false;
    }

    /**
     * The callback that is invoked when the {@link WabbajackProjectileEntity} collides with something. This is not used
     * and should not be overridden if your spell has custom nonstandard behaviour.
     * @param world the world
     * @param projectile the projectile entity
     * @param collision the collision
     * @param caster the caster, or {@code null} if summoned/thrown from dispenser.
     */
    public void onProjectileCollision(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, @Nullable LivingEntity caster) {}

    /**
     * @return whether the spell is enabled. Disabled spells will never be thrown. Defaults to {@code true}.
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return the odds that this spell will be thrown relative to the sum of all enabled spell weights. Defaults to
     * {@code 1}.
     */
    public int getWeight() {
        return 1;
    }

    /**
     * Specialized {@link Entity#discard()} method that ensures safety by preventing players from being discarded, and
     * displays a custom death message for "discarded" players and tamed entities.
     *
     * @param world the world
     * @param entity the entity
     * @param damageType the custom death message to display, or {@link ModRegistry#MADNESS} if {@code null}.
     */
    protected static void discard(ServerWorld world, Entity entity, @Nullable RegistryKey<DamageType> damageType) {
        if (entity instanceof PlayerEntity || entity instanceof Tameable tameable && tameable.getOwnerReference() != null) {
            entity.damage(world, world.getDamageSources().create(damageType != null ? damageType : ModRegistry.MADNESS), Float.MAX_VALUE);
        } else {
            entity.discard();
        }
    }
}
