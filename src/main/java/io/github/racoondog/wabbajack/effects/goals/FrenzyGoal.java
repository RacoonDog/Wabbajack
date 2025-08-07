package io.github.racoondog.wabbajack.effects.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

public class FrenzyGoal extends Goal implements WabbajackGoal {
    private final MobEntity mob;
    private final int duration;
    private final int startAge;
    private @Nullable LivingEntity target;
    private int cooldown;

    public FrenzyGoal(MobEntity mob, int duration) {
        this.mob = mob;
        this.duration = duration;
        this.startAge = mob.age;
    }

    @Override
    public boolean canStart() {
        return this.mob.age - this.startAge < duration;
    }

    @Override
    public boolean shouldContinue() {
        if (target == null) {
            this.getTarget();
            if (target == null) {
                return false;
            }
        }

        return super.shouldContinue();
    }

    @Override
    public void stop() {
        this.target = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            this.getTarget();
            if (this.target == null) {
                return;
            }
        }

        this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
        double d = this.mob.getWidth() * 2.0F * (this.mob.getWidth() * 2.0F);
        double e = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
        double f = 0.8;
        if (e > d && e < 16.0) {
            f = 1.33;
        } else if (e < 225.0) {
            f = 0.6;
        }

        this.mob.getNavigation().startMovingTo(this.target, f);
        this.cooldown = Math.max(this.cooldown - 1, 0);
        if (!(e > d)) {
            if (this.cooldown <= 0) {
                this.cooldown = 20;
                this.mob.tryAttack(getServerWorld(this.mob), this.target);
            }
        }
    }

    private void getTarget() {
        this.target = this.mob.getWorld().getOtherEntities(this.mob, this.mob.getBoundingBox().expand(10, 2, 10), LivingEntity.class::isInstance)
            .stream().map(LivingEntity.class::cast)
            .reduce((e1, e2) -> TargetUtil.getCloserEntity(this.mob, e1, e2))
            .orElse(null);
    }
}
