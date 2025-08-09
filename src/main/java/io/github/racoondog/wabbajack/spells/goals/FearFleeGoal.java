package io.github.racoondog.wabbajack.spells.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;

public class FearFleeGoal extends FleeEntityGoal<PlayerEntity> implements WabbajackGoal {
    private final int duration;
    private final int startAge;

    public FearFleeGoal(PathAwareEntity mob, LivingEntity caster, int duration) {
        super(mob, PlayerEntity.class, (LivingEntity entity) -> entity == caster, 20f, 1f, 2f, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
        this.duration = duration;
        this.startAge = mob.age;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.mob.age - this.startAge < duration;
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && this.mob.age - this.startAge < duration;
    }
}
