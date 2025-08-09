package io.github.racoondog.wabbajack.spells;

import io.github.racoondog.wabbajack.DataTags;
import io.github.racoondog.wabbajack.ParticleHelper;
import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.explosion.ExplosionImpl;
import org.jetbrains.annotations.Nullable;

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
            if (!(entity instanceof LivingEntity livingEntity)
                || (entity instanceof PlayerEntity && entity != caster)
                || entity.isInvulnerable()
                || (!entity.getType().isIn(DataTags.CAN_BE_WABBAJACKED) && entity != caster)) continue;

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

    public @Nullable SoundEvent getSound() {
        return null;
    }

    public abstract ParticleEffect getParticleEffect();

    public abstract boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster);
}
