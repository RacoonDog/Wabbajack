package io.github.racoondog.wabbajack.spells.types;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import io.github.racoondog.wabbajack.DataTags;
import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.spells.AbstractEntityAoESpell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
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

import java.util.Collections;
import java.util.List;

public class AttributeScrambleSpell extends AbstractEntityAoESpell {
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
        int attributes = Wabbajack.CONFIG.attributeScrambleSpell.attributes;

        HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> modifiers = HashMultimap.create(attributes, 1);
        for (RegistryEntry<EntityAttribute> attribute : getRandomAttributes(world.random, attributes)) {
            if (target.getAttributes().hasAttribute(attribute)) {
                modifiers.put(attribute, new EntityAttributeModifier(
                    Identifier.of(Wabbajack.MOD_ID, "attribute_scrambling"),
                    world.random.nextDouble() * Wabbajack.CONFIG.attributeScrambleSpell.magnitude * (world.random.nextBoolean() ? 1 : -1),
                    EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }
        }

        if (target instanceof PlayerEntity && modifiers.containsKey(EntityAttributes.SCALE)) {
            modifiers.putAll(EntityAttributes.CAMERA_DISTANCE, modifiers.get(EntityAttributes.SCALE));
        }

        target.getAttributes().addTemporaryModifiers(modifiers);

        if (!(target instanceof PlayerEntity) && world.random.nextInt(100) < 5) {
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

            return true;
        }

        return !modifiers.isEmpty();
    }

    private static Iterable<RegistryEntry<EntityAttribute>> getRandomAttributes(Random random, int count) {
        if (count >= Registries.ATTRIBUTE.size()) {
            return Registries.ATTRIBUTE.iterateEntries(DataTags.CAN_SCRAMBLE);
        }

        List<RegistryEntry<EntityAttribute>> list = Lists.newArrayList(Registries.ATTRIBUTE.iterateEntries(DataTags.CAN_SCRAMBLE));
        Collections.shuffle(list, java.util.Random.from(random::nextLong));
        return list.subList(0, count);
    }
}
