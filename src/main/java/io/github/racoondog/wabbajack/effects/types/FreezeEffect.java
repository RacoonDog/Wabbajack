package io.github.racoondog.wabbajack.effects.types;

import io.github.racoondog.wabbajack.Wabbajack;
import io.github.racoondog.wabbajack.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.effects.AbstractEntityAreaOfEffect;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FreezeEffect extends AbstractEntityAreaOfEffect {
    private static final UniformIntProvider FREEZE_DURATION = TimeHelper.betweenSeconds(5, 20);

    @Override
    public ParticleEffect getParticleEffect() {
        return ParticleTypes.PALE_OAK_LEAVES;
    }

    @Override
    public void onEntityEffect(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, LivingEntity target, @Nullable LivingEntity caster) {
        int duration = FREEZE_DURATION.get(world.random);

        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 9, true, false));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, duration, 127, true, false));
        target.setFrozenTicks(duration);

        if (Wabbajack.CONFIG.freezeEffect.placesBlocks) {
            Box box = target.getBoundingBox();

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            List<BlockPos> posList = new ObjectArrayList<>();

            for (int x = MathHelper.floor(box.minX); x <= MathHelper.ceil(box.maxX); x++) {
                for (int y = MathHelper.floor(box.minY); y <= MathHelper.ceil(box.maxX); y++) {
                    for (int z = MathHelper.floor(box.minZ); z <= MathHelper.ceil(box.maxX); z++) {
                        mutable.set(x, y, z);
                        BlockState state = world.getBlockState(mutable);

                        if (state.isAir() && world.getLightLevel(mutable) > 11) {
                            posList.add(mutable.toImmutable());
                        }
                    }
                }
            }

            for (BlockPos pos : posList) {
                world.setBlockState(pos, Blocks.FROSTED_ICE.getDefaultState()); // todo prevent watering
            }
        }
    }
}
