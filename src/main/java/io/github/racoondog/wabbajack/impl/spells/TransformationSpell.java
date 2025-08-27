package io.github.racoondog.wabbajack.impl.spells;

import io.github.racoondog.wabbajack.impl.*;
import io.github.racoondog.wabbajack.api.ParticleHelper;
import io.github.racoondog.wabbajack.api.spell.AbstractEntityAoESpell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

public class TransformationSpell extends AbstractEntityAoESpell {
    @Override
    public ParticleEffect getParticleEffect() {
        return new DustParticleEffect(Colors.RED, 1f);
    }

    @Override
    public @Nullable SoundEvent getSound() {
        return SoundEvents.ITEM_TRIDENT_THUNDER.value();
    }

    @Override
    public boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        if (target instanceof PlayerEntity) return false;

        discard(world, target, ModRegistry.DISFIGURED);
        Entity entity = DataTags.getRandom(Registries.ENTITY_TYPE, DataTags.CAN_BE_WABBAJACKED, world.random, () -> EntityType.TADPOLE.getRegistryEntry(), target.getType().getRegistryEntry()).value()
            .spawn(world, null, target.getBlockPos(), SpawnReason.MOB_SUMMONED, false, false);

        if (entity != null) {
            ParticleHelper.spawnEmotionParticles(world, entity, ParticleTypes.RAID_OMEN);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        return Wabbajack.CONFIG.spellToggles.transformation;
    }

    @Override
    public int getWeight() {
        return Wabbajack.CONFIG.spellWeights.transformation;
    }
}
