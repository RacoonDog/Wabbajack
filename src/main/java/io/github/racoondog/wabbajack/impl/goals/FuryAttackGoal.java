package io.github.racoondog.wabbajack.impl.goals;

import io.github.racoondog.wabbajack.api.WabbajackGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.mob.MobEntity;

public class FuryAttackGoal extends AttackGoal implements WabbajackGoal {
    private final MobEntity mob;
    private final int duration;
    private final int startAge;

    public FuryAttackGoal(MobEntity mob, int duration) {
        super(mob);
        this.mob = mob;
        this.duration = duration;
        this.startAge = mob.age;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.mob.age - this.startAge < this.getDuration();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && this.mob.age - this.startAge < this.getDuration();
    }

    @Override
    public int getDuration() {
        return this.duration;
    }
}
