package io.github.racoondog.wabbajack.effects.types;

import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.AbstractEntityAreaOfEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

public class DisintegrationEffect extends AbstractEntityAreaOfEffect {
    public static final RegistryKey<DamageType> DISINTEGRATED = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Wabbajack.MOD_ID, "disintegrated"));
    public static final RegistryKey<DamageType> MADNESS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Wabbajack.MOD_ID, "madness"));

    @Override
    public ParticleEffect getParticleEffect() {
        return ParticleTypes.SMOKE;
    }

    @Override
    public @Nullable SoundEvent getSound() {
        return SoundEvents.ENTITY_BREEZE_DEATH;
    }

    @Override
    public boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        if (target instanceof PlayerEntity || target instanceof Tameable tameable && tameable.getOwnerReference() != null) {
            target.damage(world, world.getDamageSources().create(world.random.nextBoolean() ? DISINTEGRATED : MADNESS), Float.MAX_VALUE);
        } else {
            target.discard();
        }

        return true;
    }
}
