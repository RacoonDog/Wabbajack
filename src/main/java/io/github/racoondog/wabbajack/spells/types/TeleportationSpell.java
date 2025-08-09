package io.github.racoondog.wabbajack.spells.types;

import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.spells.AbstractEntityAoESpell;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

public class TeleportationSpell extends AbstractEntityAoESpell {
    @Override
    public ParticleEffect getParticleEffect() {
        return ParticleTypes.PORTAL;
    }

    @Override
    public @Nullable SoundEvent getSound() {
        return SoundEvents.ENTITY_PLAYER_TELEPORT;
    }

    @Override
    public boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        for (ConsumeEffect effect : ConsumableComponents.CHORUS_FRUIT.onConsumeEffects()) {
            effect.onConsume(world, null, target);
        }

        return true;
    }
}
