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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class AttributeScrambleEffect extends AbstractEntityAreaOfEffect {
    @Override
    public ParticleEffect getParticleEffect() {
        return new DustParticleEffect(Colors.LIGHT_PINK, 1f);
    }

    @Override
    public @Nullable SoundEvent getSound() {
        return SoundEvents.ENTITY_WITCH_CELEBRATE;
    }

    @Override
    public boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        int attributes = Wabbajack.CONFIG.attributeScrambleEffect.attributes;

        HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = HashMultimap.create(attributes, 1);
        for (RegistryEntry<EntityAttribute> attribute : getRandomAttributes(world.random, attributes)) {
            if (target.getAttributes().hasAttribute(attribute)) {
                modifiers.put(attribute, new EntityAttributeModifier(
                    Identifier.of(Wabbajack.MOD_ID, "attribute_scrambling"),
                    world.random.nextDouble() * Wabbajack.CONFIG.attributeScrambleEffect.magnitude * (world.random.nextBoolean() ? 1 : -1),
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }
        }

        target.getAttributes().addTemporaryModifiers(modifiers);

        boolean dinnerbone = world.random.nextInt(100) < 5;
        if (dinnerbone) {
            target.setCustomName(Text.empty()
                .append(Text.literal("D").withColor(0xFFABAB))
                .append(Text.literal("i").withColor(0xFFD9AB))
                .append(Text.literal("n").withColor(0xF5FFAB))
                .append(Text.literal("n").withColor(0xC7FFAB))
                .append(Text.literal("e").withColor(0xABFFBD))
                .append(Text.literal("r").withColor(0xABFFED))
                .append(Text.literal("b").withColor(0xABE3FF))
                .append(Text.literal("o").withColor(0xABB5FF))
                .append(Text.literal("n").withColor(0xD1ABFF))
                .append(Text.literal("e").withColor(0xFFABFF))
            );
        }

        return !modifiers.isEmpty() || dinnerbone;
    }

    private static Iterable<RegistryEntry<EntityAttribute>> getRandomAttributes(Random random, int count) {
        if (count >= Registries.ATTRIBUTE.size()) {
            return Registries.ATTRIBUTE.iterateEntries(DataTags.CAN_SCRAMBLE);
        }

        Set<RegistryEntry<EntityAttribute>> set = new ReferenceOpenHashSet<>();

        int depth = 0;
        while (set.size() < count) {
            if (depth++ == 10) {
                break;
            }

            Optional<RegistryEntry.Reference<EntityAttribute>> entry = Registries.ATTRIBUTE.getEntry(random.nextInt(Registries.ATTRIBUTE.size()));
            if (entry.isPresent() && entry.get().isIn(DataTags.CAN_SCRAMBLE)) {
                set.add(entry.get());
            }
        }

        return set;
    }
}
