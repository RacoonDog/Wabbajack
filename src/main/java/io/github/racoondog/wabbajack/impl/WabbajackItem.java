package io.github.racoondog.wabbajack.impl;

import io.github.racoondog.wabbajack.api.spell.WabbajackSpell;
import io.github.racoondog.wabbajack.impl.compat.arealib.WabbajackAreaComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
                @Nullable WabbajackSpell spell = Wabbajack.getSpell(world.random, true);

                if (spell == null) {
                    return false;
                }

                spell.onItemUse(serverWorld, playerEntity, stack);
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
        if (world.isClient() && Wabbajack.HAS_AREALIB && WabbajackAreaComponent.shouldDisableWabbajack(world, user.getPos())) {
            world.playSoundClient(SoundEvents.ENTITY_WARDEN_HEARTBEAT, SoundCategory.AMBIENT, 1.0f, 1.0f);
            return ActionResult.PASS;
        }

        if (user instanceof ServerPlayerEntity serverPlayer && (Wabbajack.SPELLS.isEmpty() || (Wabbajack.HAS_AREALIB && WabbajackAreaComponent.shouldDisableWabbajack(world, user.getPos())))) {
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                Text.translatable(
                    "actionbar.wabbajack.disabled",
                    Text.translatable("actionbar.wabbajack.title").formatted(Formatting.LIGHT_PURPLE)
                ).formatted(Formatting.DARK_PURPLE))
            );

            if (Wabbajack.SPELLS.isEmpty()) {
                serverPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(
                    Registries.SOUND_EVENT.getEntry(SoundEvents.ENTITY_WARDEN_HEARTBEAT),
                    SoundCategory.AMBIENT,
                    serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                    1.0f, 1.0f, world.getRandom().nextLong()
                ));
            }

            return ActionResult.PASS;
        }

        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        return new WabbajackProjectileEntity(world, pos.getX(), pos.getY(), pos.getZ());
    }
}
