package io.github.racoondog.wabbajack.api;

import io.github.racoondog.wabbajack.impl.DataTags;
import io.github.racoondog.wabbajack.impl.ModRegistry;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import io.github.racoondog.wabbajack.impl.WabbajackItem;
import io.github.racoondog.wabbajack.api.spell.WabbajackSpell;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.random.Random;

import java.util.Optional;

public interface WabbajackApi {
    /**
     * The Wabbajack! item.
     */
    WabbajackItem WABBAJACK_ITEM = ModRegistry.WABBAJACK_ITEM;

    /**
     * The {@link TagKey} that controls which entities can be affected by
     * {@link io.github.racoondog.wabbajack.api.spell.AbstractEntityAoESpell}.
     */
    TagKey<EntityType<?>> CAN_BE_WABBAJACKED = DataTags.CAN_BE_WABBAJACKED;

    /**
     * Registers a spell to The Wabbajack! spell pool.
     *
     * @param spell the spell to register
     */
    static void registerSpell(WabbajackSpell spell) {
        Wabbajack.SPELL_REGISTRY.add(spell);
        Wabbajack.updateSpellPool();
    }

    /**
     * Generates a random spell from The Wabbajack! spell pool. Can return {@link Optional#empty()} if there are no
     * valid spells to choose, such as when all spells are disabled from the config, or all enabled spells require a
     * caster, but {@code hasCaster} is {@code false}.
     *
     * @param random the random
     * @param hasCaster whether the spell will have a caster. Some spells require caster entities to function correctly.
     * @return the spell, or {@link Optional#empty()} if the current config & context result in no valid spells.
     */
    static Optional<WabbajackSpell> getSpell(Random random, boolean hasCaster) {
        return Optional.ofNullable(Wabbajack.getSpell(random, hasCaster));
    }
}
