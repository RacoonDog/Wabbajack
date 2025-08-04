package io.github.racoondog.wabbajack.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.racoondog.wabbajack.ModRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    /**
     * @reason We want separate first person and third person animations for The Wabbajack!
     */
    @WrapOperation(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/item/consume/UseAction;"))
    private UseAction wrap(ItemStack instance, Operation<UseAction> original) {
        if (instance.isOf(ModRegistry.WABBAJACK_ITEM)) {
            return UseAction.BOW;
        } else {
            return original.call(instance);
        }
    }
}
