package io.github.racoondog.wabbajack.impl.spells;

import io.github.racoondog.wabbajack.impl.Wabbajack;
import io.github.racoondog.wabbajack.impl.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.api.spell.WabbajackSpell;
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

    @Override
    public boolean isEnabled() {
        return Wabbajack.CONFIG.spellToggles.fireball;
    }

    @Override
    public int getWeight() {
        return Wabbajack.CONFIG.spellWeights.fireball;
    }
}
