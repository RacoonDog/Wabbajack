package io.github.racoondog.wabbajack.api;

/**
 * A marker for {@link net.minecraft.entity.ai.goal.Goal}s that are added from
 * {@link io.github.racoondog.wabbajack.api.spell.WabbajackSpell}, so that they may easily be wiped from entities when
 * a conflicting {@link WabbajackGoal} is applied. Every Wabbajack added goal should implement this interface.
 */
public interface WabbajackGoal {
    /**
     * Every {@link io.github.racoondog.wabbajack.api.spell.WabbajackSpell} should be temporary, therefore the goals
     * added by them should have a set duration before they stop acting on the entity. This method does nothing outside
     * of acting as a reminder of that.
     *
     * @return the duration of the {@link io.github.racoondog.wabbajack.api.spell.WabbajackSpell} that created this goal.
     */
    int getDuration();
}
