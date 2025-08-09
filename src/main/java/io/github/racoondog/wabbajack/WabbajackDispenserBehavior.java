package io.github.racoondog.wabbajack;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;

public class WabbajackDispenserBehavior extends ItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ProjectileItem.Settings projectileSettings = ModRegistry.WABBAJACK_ITEM.getProjectileSettings();
        ServerWorld world = pointer.world();
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        Position position = projectileSettings.positionFunction().getDispensePosition(pointer, direction);

        Wabbajack.getSpell(world, false).onDispense(world, direction, position, stack);
        stack.damage(1, world, null, item -> {});

        return stack;
    }

    @Override
    protected void playSound(BlockPointer pointer) {
        pointer.world().playSound(null, pointer.pos(), SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
