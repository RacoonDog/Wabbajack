package io.github.racoondog.wabbajack;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ParticleHelper {
    public static void spawnEmotionParticles(ServerWorld world, Entity entity, ParticleEffect effect) {
        world.spawnParticles(
            effect,
            entity.getX(),
            entity.getBodyY(0.5d) + 1.0d,
            entity.getZ(),
            5,
            0.2,
            0.2,
            0.2,
            0.1f
        );
    }

    public static void spawnAreaParticles(ServerWorld world, HitResult result, double size, ParticleEffect effect) {
        Vec3d pos = result.getPos();

        double horizontalOffset = 1d / 5d * size;
        double verticalOffset = 1d / 5d * size;

        if (result instanceof BlockHitResult blockHitResult) {
            Direction side = blockHitResult.getSide();
            pos = pos.add(0.1d * side.getOffsetX(), 0.1d * side.getOffsetY(), 0.1d * side.getOffsetZ());

            if (side.getAxis() == Direction.Axis.Y) {
                verticalOffset = 1d / 30d;
                horizontalOffset = 1d / 3d * size;
            }
        }

        world.spawnParticles(
            effect,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            (int) (size * size + 4 * size + 25),
            horizontalOffset,
            verticalOffset,
            horizontalOffset,
            0.1f
        );
    }
}
