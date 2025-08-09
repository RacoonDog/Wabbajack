package io.github.racoondog.wabbajack.impl.spells;

import com.google.common.collect.HashMultimap;
import io.github.racoondog.wabbajack.api.ParticleHelper;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import io.github.racoondog.wabbajack.impl.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.api.spell.AbstractEntityAoESpell;
import io.github.racoondog.wabbajack.impl.goals.FrenzyGoal;
import io.github.racoondog.wabbajack.api.WabbajackGoal;
import io.github.racoondog.wabbajack.impl.mixin.AttributeContainerAccessor;
import io.github.racoondog.wabbajack.impl.mixin.MobEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.jetbrains.annotations.Nullable;

public class FrenzySpell extends AbstractEntityAoESpell {
    private static final UniformIntProvider FRENZY_DURATION = TimeHelper.betweenSeconds(5, 20);

    @Override
    public @Nullable SoundEvent getSound() {
        return SoundEvents.ENTITY_ENDERMAN_SCREAM;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ParticleEffect getParticleEffect() {
        return new DustParticleEffect(Formatting.LIGHT_PURPLE.getColorValue(), 1f);
    }

    @Override
    public boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        if (target instanceof MobEntity mobEntity) {
            int duration = FRENZY_DURATION.get(world.random);

            mobEntity.setTarget(caster);
            mobEntity.setAttacking(true);

            GoalSelector goalSelector = ((MobEntityAccessor) mobEntity).wabbajack$getGoalSelector();
            goalSelector.clear(goal -> goal instanceof WabbajackGoal);
            goalSelector.add(1, new FrenzyGoal(mobEntity, duration));

            if (!mobEntity.getAttributes().hasAttribute(EntityAttributes.ATTACK_DAMAGE)) {
                ((AttributeContainerAccessor) mobEntity.getAttributes()).wabbajack$getCustom().put(
                    EntityAttributes.ATTACK_DAMAGE,
                    new EntityAttributeInstance(EntityAttributes.ATTACK_DAMAGE, instance -> {})
                );

                mobEntity.getAttributes().addTemporaryModifiers(createAttackDamageAttribute());
            }

            target.getBrain().forget(MemoryModuleType.AVOID_TARGET);
            target.getBrain().forget(MemoryModuleType.IS_PANICKING);
            target.getBrain().doExclusively(Activity.FIGHT);

            ParticleHelper.spawnEmotionParticles(world, target, ParticleTypes.WITCH);
            return true;
        } else {
            return false;
        }
    }

    private static HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> createAttackDamageAttribute() {
        HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> multimap = HashMultimap.create();
        multimap.put(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(
            Identifier.of(Wabbajack.MOD_ID, "attack_damage"),
            1.5d,
            EntityAttributeModifier.Operation.ADD_VALUE
        ));
        return multimap;
    }

    @Override
    public boolean isEnabled() {
        return Wabbajack.CONFIG.spellToggles.frenzy;
    }

    @Override
    public int getWeight() {
        return Wabbajack.CONFIG.spellWeights.frenzy;
    }
}
