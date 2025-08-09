package io.github.racoondog.wabbajack.impl.spells;

import io.github.racoondog.wabbajack.api.spell.WabbajackSpell;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import io.github.racoondog.wabbajack.impl.WabbajackProjectileEntity;
import io.github.racoondog.wabbajack.impl.mixin.FireworkRocketEntityInvoker;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfettiSpell extends WabbajackSpell {
    private static final FireworkExplosionComponent.Type[] EXPLOSION_TYPES = FireworkExplosionComponent.Type.values();

    @Override
    public void onProjectileCollision(ServerWorld world, WabbajackProjectileEntity projectile, HitResult collision, @Nullable LivingEntity caster) {
        ItemStack stack = Items.FIREWORK_ROCKET.getDefaultStack();

        stack.set(DataComponentTypes.FIREWORKS, new FireworksComponent(0, List.of(
            generateRandomFirework(world.random),
            generateRandomFirework(world.random),
            generateRandomFirework(world.random)
        )));

        Vec3d pos = collision.getPos();
        FireworkRocketEntity entity = new FireworkRocketEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        entity.setOwner(caster);
        world.spawnEntity(entity);

        ((FireworkRocketEntityInvoker) entity).wabbajack$invokeExplodeAndRemove(world);
    }

    private FireworkExplosionComponent generateRandomFirework(Random random) {
        return new FireworkExplosionComponent(
            EXPLOSION_TYPES[random.nextInt(EXPLOSION_TYPES.length)],
            IntList.of(colour(random), colour(random), colour(random)),
            random.nextBoolean() ? IntList.of() : IntList.of(colour(random)),
            false,
            random.nextBoolean()
        );
    }

    private int colour(Random random) {
        return random.nextBetween(0, 0xFFFFFF);
    }

    @Override
    public boolean isEnabled() {
        return Wabbajack.CONFIG.spellToggles.confetti;
    }

    @Override
    public int getWeight() {
        return Wabbajack.CONFIG.spellWeights.confetti;
    }
}
