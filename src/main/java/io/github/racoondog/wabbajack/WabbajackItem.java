package io.github.racoondog.wabbajack;

import io.github.racoondog.wabbajack.effects.WabbajackEffect;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class WabbajackItem extends Item implements ProjectileItem {
    private final ProjectileItem.Settings projectileSettings = ProjectileItem.Settings.builder()
        .uncertainty(2.5f)
        .build();

    public WabbajackItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.wabbajack.wabbajack.desc").formatted(Formatting.DARK_RED));
    }

    @Override
    public ProjectileItem.Settings getProjectileSettings() {
        return this.projectileSettings;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (i < 10 || stack.willBreakNextUse()) {
                return false;
            }

            if (world instanceof ServerWorld serverWorld) {
                WabbajackEffect effect = Wabbajack.getEffect(world, true);
                effect.onItemUse(serverWorld, playerEntity, stack);

                stack.damage(1, playerEntity);

                return true;
            }
            return false;
        } else {
            return super.onStoppedUsing(stack, world, user, remainingUseTicks);
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        return new WabbajackProjectileEntity(world, pos.getX(), pos.getY(), pos.getZ());
    }
}
