package io.github.racoondog.wabbajack.effects.types;

import io.github.racoondog.wabbajack.ParticleHelper;
import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.AbstractEntityAreaOfEffect;
import io.github.racoondog.wabbajack.effects.goals.FearFleeGoal;
import io.github.racoondog.wabbajack.effects.goals.WabbajackGoal;
import io.github.racoondog.wabbajack.mixin.MobEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Colors;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.jetbrains.annotations.Nullable;

public class FearEffect extends AbstractEntityAreaOfEffect {
    private static final UniformIntProvider AVOID_DURATION = TimeHelper.betweenSeconds(5, 20);

    @Override
    public ParticleEffect getParticleEffect() {
        return new DustParticleEffect(Colors.BLUE, 1f);
    }

    @Override
    public @Nullable SoundEvent getSound() {
        return SoundEvents.ENTITY_WARDEN_AGITATED;
    }

    @Override
    public float getAreaSize() {
        return Wabbajack.CONFIG.fearEffect.areaSize;
    }

    @Override
    public void onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        int duration = AVOID_DURATION.get(world.random);

        if (target instanceof MobEntity mobEntity) {
            mobEntity.setTarget(null);
            mobEntity.setAttacking(false);

            if (target instanceof PathAwareEntity pae) {
                GoalSelector goalSelector = ((MobEntityAccessor) mobEntity).wabbajack$getGoalSelector();
                goalSelector.clear(goal -> goal instanceof WabbajackGoal);
                goalSelector.add(1, new FearFleeGoal(pae, caster, duration));
            }
        }

        target.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        target.getBrain().remember(MemoryModuleType.AVOID_TARGET, caster, duration);
        target.getBrain().remember(MemoryModuleType.IS_PANICKING, true, duration);
        target.getBrain().doExclusively(Activity.AVOID);

        ParticleHelper.spawnEmotionParticles(world, target, ParticleTypes.SOUL);
    }
}
