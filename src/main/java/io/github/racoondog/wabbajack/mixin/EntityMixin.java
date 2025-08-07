package io.github.racoondog.wabbajack.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract double getY();
    @Shadow public abstract World getWorld();
    @Shadow protected abstract void tickInVoid();

    @SuppressWarnings("ConstantValue")
    @Inject(method = "attemptTickInVoid", at = @At("TAIL"))
    private void cleanUpSpaceDebris(CallbackInfo ci) {
        if (this.getY() > this.getWorld().getBottomY() + this.getWorld().getHeight() + 64
            && (Object) this instanceof LivingEntity livingEntity
            && livingEntity.getAttributes().hasAttribute(EntityAttributes.GRAVITY)
            && livingEntity.getAttributes().getValue(EntityAttributes.GRAVITY) <= 0) {
            this.tickInVoid();
        }
    }
}
