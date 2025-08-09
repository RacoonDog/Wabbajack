package io.github.racoondog.wabbajack.impl.mixin;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireworkRocketEntity.class)
public interface FireworkRocketEntityInvoker {
    @Invoker("explodeAndRemove")
    void wabbajack$invokeExplodeAndRemove(ServerWorld world);
}
