package io.github.racoondog.wabbajack.effects.types;

import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.WabbajackEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ThunderboltEffect extends WabbajackEffect {
    @Override
    public void onProjectileCollision(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, @Nullable LivingEntity caster) {
        world.spawnEntity(EntityType.LIGHTNING_BOLT.spawn(world, BlockPos.ofFloored(collision.getPos()), SpawnReason.TRIGGERED));
    }
}
