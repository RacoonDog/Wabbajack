package io.github.racoondog.wabbajack.api;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ParticleHelper {
    /**
     * Displays particle effects randomly around the entity's head.
     *
     * @param world the world
     * @param entity the entity
     * @param effect the particle effects to display
     */
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

    /**
     * Displays particle effects in a collision area. Collisions with the top/bottom of a block spread out particles
     * more than with the sides, and the particle count is automatically calculated from the area size based on a cubic
     * function.
     *
     * @param world the world
     * @param hitResult the collision
     * @param areaSize the area size
     * @param effect the particle effects to display
     */
    public static void spawnAreaParticles(ServerWorld world, HitResult hitResult, double areaSize, ParticleEffect effect) {
        Vec3d pos = hitResult.getPos();

        double horizontalOffset = 1d / 5d * areaSize;
        double verticalOffset = 1d / 5d * areaSize;

        if (hitResult instanceof BlockHitResult blockHitResult) {
            Direction side = blockHitResult.getSide();
            pos = pos.add(0.1d * side.getOffsetX(), 0.1d * side.getOffsetY(), 0.1d * side.getOffsetZ());

            if (side.getAxis() == Direction.Axis.Y) {
                verticalOffset = 1d / 30d;
                horizontalOffset = 1d / 3d * areaSize;
            }
        }

        world.spawnParticles(
            effect,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            (int) (areaSize * areaSize + 4 * areaSize + 25),
            horizontalOffset,
            verticalOffset,
            horizontalOffset,
            0.1f
        );
    }
}
