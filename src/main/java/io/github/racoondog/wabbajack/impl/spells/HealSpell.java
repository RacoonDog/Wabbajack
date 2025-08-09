package io.github.racoondog.wabbajack.impl.spells;

import io.github.racoondog.wabbajack.api.ParticleHelper;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import io.github.racoondog.wabbajack.impl.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.api.spell.AbstractEntityAoESpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

public class HealSpell extends AbstractEntityAoESpell {
    @Override
    public ParticleEffect getParticleEffect() {
        return new DustParticleEffect(Colors.YELLOW, 1f);
    }

    @Override
    public @Nullable SoundEvent getSound() {
        return SoundEvents.ENTITY_GENERIC_EAT.value();
    }

    @Override
    public boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        target.heal(5f);

        ParticleHelper.spawnEmotionParticles(world, target, ParticleTypes.HEART);
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Wabbajack.CONFIG.spellToggles.heal;
    }

    @Override
    public int getWeight() {
        return Wabbajack.CONFIG.spellWeights.heal;
    }
}
