package io.github.racoondog.wabbajack.effects.types;

import io.github.racoondog.wabbajack.ParticleHelper;
import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.AbstractEntityAreaOfEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

public class HealEffect extends AbstractEntityAreaOfEffect {
    @Override
    public ParticleEffect getParticleEffect() {
        return new DustParticleEffect(Colors.YELLOW, 1f);
    }

    @Override
    public float getAreaSize() {
        return Wabbajack.CONFIG.healEffect.areaSize;
    }

    @Override
    public void onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        target.heal(5f);

        ParticleHelper.spawnEmotionParticles(world, target, ParticleTypes.HEART);
    }
}
