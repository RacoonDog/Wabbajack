package io.github.racoondog.wabbajack.effects.types;

import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.WabbajackEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireballEffect extends WabbajackEffect {
    @Override
    public void onProjectileCollision(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, @Nullable LivingEntity caster) {
        world.createExplosion(
            projectile,
            projectile.getX(),
            projectile.getY(),
            projectile.getZ(),
            Wabbajack.CONFIG.fireballEffect.explosionPower,
            World.ExplosionSourceType.MOB
        );
    }
}
