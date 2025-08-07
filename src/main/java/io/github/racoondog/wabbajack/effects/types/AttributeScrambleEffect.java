package io.github.racoondog.wabbajack.effects.types;

import com.google.common.collect.HashMultimap;
import io.github.racoondog.wabbajack.DataTags;
import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.AbstractEntityAreaOfEffect;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AttributeScrambleEffect extends AbstractEntityAreaOfEffect {
    @Override
    public ParticleEffect getParticleEffect() {
        return new DustParticleEffect(Colors.LIGHT_PINK, 1f);
    }

    @Override
    public boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        int attributes = Wabbajack.CONFIG.attributeScrambleEffect.attributes;

        HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = HashMultimap.create(attributes, 1);
        for (RegistryEntry<EntityAttribute> attribute : getRandomAttributes(attributes)) {
            if (target.getAttributes().hasAttribute(attribute)) {
                modifiers.put(attribute, new EntityAttributeModifier(
                    Identifier.of(Wabbajack.MOD_ID, "attribute_scrambling"),
                    world.random.nextDouble() * Wabbajack.CONFIG.attributeScrambleEffect.magnitude,
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }
        }

        return !modifiers.isEmpty();
    }

    private static Iterable<RegistryEntry<EntityAttribute>> getRandomAttributes(int count) {
        if (count >= Registries.ATTRIBUTE.size()) {
            return Registries.ATTRIBUTE.iterateEntries(DataTags.CAN_SCRAMBLE);
        }

        Set<RegistryEntry<EntityAttribute>> set = new ReferenceOpenHashSet<>();

        int depth = 0;
        while (set.size() < count) {
            if (depth++ == 10) {
                break;
            }

            Optional<RegistryEntry.Reference<EntityAttribute>> entry = Registries.ATTRIBUTE.getEntry(ThreadLocalRandom.current().nextInt(Registries.ATTRIBUTE.size()));
            if (entry.isPresent() && entry.get().isIn(DataTags.CAN_SCRAMBLE)) {
                set.add(entry.get());
            }
        }

        return set;
    }
}
