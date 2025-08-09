package io.github.racoondog.wabbajack.impl.spells;

import io.github.racoondog.wabbajack.impl.Wabbajack;
import io.github.racoondog.wabbajack.impl.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.api.spell.WabbajackSpell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class ThunderboltSpell extends WabbajackSpell {
    @Override
    public void onProjectileCollision(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, @Nullable LivingEntity caster) {
        float aoe = Wabbajack.CONFIG.aoeSize;
        int strikes = MathHelper.ceil(0.2f * aoe * aoe + 1f);

        world.spawnEntity(EntityType.LIGHTNING_BOLT.spawn(world, BlockPos.ofFloored(collision.getPos()), SpawnReason.TRIGGERED));

        if (strikes > 1) {
            for (int i = 1; i < strikes; i++) {
                double x = world.random.nextGaussian() * aoe / 2;
                double z = world.random.nextGaussian() * aoe / 2;

                world.spawnEntity(EntityType.LIGHTNING_BOLT.spawn(world, BlockPos.ofFloored(collision.getPos().add(x, 0, z)), SpawnReason.TRIGGERED));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return Wabbajack.CONFIG.spellToggles.thunderbolt;
    }

    @Override
    public int getWeight() {
        return Wabbajack.CONFIG.spellWeights.thunderbolt;
    }
}
