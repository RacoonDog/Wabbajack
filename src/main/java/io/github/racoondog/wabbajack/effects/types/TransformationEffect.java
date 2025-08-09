package io.github.racoondog.wabbajack.effects.types;

import io.github.racoondog.wabbajack.DataTags;
import io.github.racoondog.wabbajack.ModRegistry;
import io.github.racoondog.wabbajack.ParticleHelper;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.AbstractEntityAreaOfEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TransformationEffect extends AbstractEntityAreaOfEffect {
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
        Entity entity = getRandomEntity(world.random, target.getType()).spawn(world, null, target.getBlockPos(), SpawnReason.MOB_SUMMONED, false, false);

        if (entity != null) {
            ParticleHelper.spawnEmotionParticles(world, entity, ParticleTypes.RAID_OMEN);
            return true;
        } else {
            return false;
        }
    }

    private static EntityType<? extends Entity> getRandomEntity(Random random, EntityType<?> current) {
        if (Registries.ENTITY_TYPE.iterateEntries(DataTags.CAN_BE_WABBAJACKED) instanceof RegistryEntryList<EntityType<?>> registryEntryList
            && !(registryEntryList.size() <= 1 && current.isIn(registryEntryList))) { // make sure list does not only contain `current`, or else it would loop infinitely

            Optional<EntityType<? extends Entity>> entry;
            do {
                entry = registryEntryList.getRandom(random).map(RegistryEntry::value);
            } while (entry.isPresent() && entry.get() == current);

            return entry.isPresent() ? entry.get() : EntityType.TADPOLE;
        } else {
            return EntityType.TADPOLE;
        }
    }
}
