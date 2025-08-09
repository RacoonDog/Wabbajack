package io.github.racoondog.wabbajack.spells.types;

import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.spells.WabbajackSpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireballSpell extends WabbajackSpell {
    @Override
    public void onProjectileCollision(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, @Nullable LivingEntity caster) {
        world.createExplosion(
            projectile,
            projectile.getX(),
            projectile.getY(),
            projectile.getZ(),
            Wabbajack.CONFIG.fireballSpell.explosionPower,
            World.ExplosionSourceType.MOB
        );
    }
}
