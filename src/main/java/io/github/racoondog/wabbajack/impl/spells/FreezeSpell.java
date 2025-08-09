package io.github.racoondog.wabbajack.impl.spells;

import io.github.racoondog.wabbajack.impl.ModRegistry;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import io.github.racoondog.wabbajack.impl.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.api.spell.AbstractEntityAoESpell;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.jetbrains.annotations.Nullable;

public class FreezeSpell extends AbstractEntityAoESpell {
    private static final UniformIntProvider FREEZE_DURATION = TimeHelper.betweenSeconds(5, 20);

    @Override
    public ParticleEffect getParticleEffect() {
        return ParticleTypes.PALE_OAK_LEAVES;
    }

    @Override
    public @Nullable SoundEvent getSound() {
        return SoundEvents.BLOCK_GLASS_BREAK;
    }

    @Override
    public boolean onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        int duration = FREEZE_DURATION.get(world.random);

        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 9, true, false));
        target.setFrozenTicks(duration);

        if (Wabbajack.CONFIG.freezeSpell.placesBlocks) {
            Box box = target.getBoundingBox();

            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for (int x = MathHelper.floor(box.minX); x <= MathHelper.ceil(box.maxX); x++) {
                for (int y = MathHelper.floor(box.minY); y <= MathHelper.ceil(box.maxY); y++) {
                    for (int z = MathHelper.floor(box.minZ); z <= MathHelper.ceil(box.maxZ); z++) {
                        mutable.set(x, y, z);
                        BlockState state = world.getBlockState(mutable);

                        if (state.isAir()) {
                            world.setBlockState(mutable, ModRegistry.FROST_BLOCK.getDefaultState());
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean isEnabled() {
        return Wabbajack.CONFIG.spellToggles.freeze;
    }

    @Override
    public int getWeight() {
        return Wabbajack.CONFIG.spellWeights.freeze;
    }
}
