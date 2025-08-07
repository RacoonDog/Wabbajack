package io.github.racoondog.wabbajack.effects.types;

import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.AbstractEntityAreaOfEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

public class DisintegrationEffect extends AbstractEntityAreaOfEffect {
    @Override
    public ParticleEffect getParticleEffect() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public @Nullable SoundEvent getSound() {
        return SoundEvents.ENTITY_BREEZE_DEATH;
    }

    @Override
    public void onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        target.discard();
    }
}
