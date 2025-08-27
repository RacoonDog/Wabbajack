package io.github.racoondog.wabbajack.api.spell;

import io.github.racoondog.wabbajack.impl.DataTags;
import io.github.racoondog.wabbajack.api.ParticleHelper;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import io.github.racoondog.wabbajack.impl.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.impl.compat.arealib.WabbajackAreaComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.explosion.ExplosionImpl;
import net.modfest.fireblanket.Fireblanket;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A type of {@link WabbajackSpell} that affects entities in an area of effect around where the projectile landed.
 * This type of spell uses the default projectile.
 */
public abstract class AbstractEntityAoESpell extends WabbajackSpell {
    @Override
    public void onProjectileCollision(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, @Nullable LivingEntity caster) {
        Entity hit = null;
        if (collision instanceof EntityHitResult entityHitResult) {
            hit = entityHitResult.getEntity();
        }

        float areaSize = Wabbajack.CONFIG.aoeSize;

        boolean affected = false;
        for (Entity entity : world.getOtherEntities(projectile, projectile.getBoundingBox().expand(areaSize))) {
            if (!canAffect(entity, caster)) continue;
            LivingEntity livingEntity = (LivingEntity) entity;

            if (entity == hit || (entity.squaredDistanceTo(projectile) < areaSize * areaSize && ExplosionImpl.calculateReceivedDamage(projectile.getPos(), entity) > 0.1f)) {
                affected |= this.onEntityEffect(world, projectile, collision, livingEntity, caster);
            }
        }

        ParticleHelper.spawnAreaParticles(world, collision, areaSize, this.getParticleEffect());
        if (affected && this.getSound() != null) {
            world.playSound(null, collision.getPos().getX(), collision.getPos().getY(), collision.getPos().getZ(),
                this.getSound(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    public boolean canAffect(Entity entity, @Nullable LivingEntity caster) {
        boolean selfHit = entity == caster;

        // target has to be living
        if (!(entity instanceof LivingEntity)) {
            return false;
        }

        // target cant be player, unless pvp or self hit
        // for arealib, both the caster and target need to be in a pvp enabled zone
        boolean globalPvp = Wabbajack.CONFIG.pvp;
        boolean pvp = Wabbajack.HAS_AREALIB
            ? WabbajackAreaComponent.canPvp(entity.getWorld(), entity.getPos()).orElse(globalPvp) && (caster == null || WabbajackAreaComponent.canPvp(caster.getWorld(), caster.getPos()).orElse(globalPvp))
            : globalPvp;
        if (entity instanceof PlayerEntity && !selfHit && !pvp) {
            return false;
        }

        // target cant be invulnerable, unless fireblanket in which case all entities are invulnerable
        if (entity.isInvulnerable() && !Wabbajack.HAS_FIREBLANKET && !entity.getWorld().getServer().getGameRules().getBoolean(Fireblanket.NEW_ENTITIES_IMMUTABLE)) {
            return false;
        }

        // target has to be wabbajackable, unless self hit
        if (selfHit) return true;

        if (Wabbajack.HAS_AREALIB) {
            Set<EntityType<?>> canBeWabbajacked = WabbajackAreaComponent.canBeWabbajacked(entity.getWorld(), entity.getPos());
            if (canBeWabbajacked.contains(entity.getType())) {
                return true;
            } else {
                return entity.getType().isIn(DataTags.CAN_BE_WABBAJACKED) && !WabbajackAreaComponent.cannotBeWabbajacked(entity.getWorld(), entity.getPos()).contains(entity.getType());
            }
        } else {
            return entity.getType().isIn(DataTags.CAN_BE_WABBAJACKED);
        }
    }

    /**
     * @return the sound to play on collision, or {@code null} if silent.
     */
    public @Nullable SoundEvent getSound() {
        return null;
    }

    /**
     * @return the {@link ParticleEffect} to display in the collision area.
     */
    public abstract ParticleEffect getParticleEffect();

    /**
     * This callback is run for every {@link LivingEntity} that can be hit (e.g. Wabbajackable and not invincible) in
     * the area of effect zone.
     *
     * @param world the world
     * @param projectile the projectile entity
     * @param collision the collision which triggered the area of effect
     * @param target the entity being affected
     * @param caster the caster, or {@code null} if summoned/thrown from dispenser.
     * @return {@code true} if the entity was affected. Sounds are only played if an entity was affected.
     */
    public abstract boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster);
}
