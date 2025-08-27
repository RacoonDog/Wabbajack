package io.github.racoondog.wabbajack.impl.compat.arealib;

import dev.doublekekse.area_lib.component.AreaDataComponent;
import dev.doublekekse.area_lib.component.AreaDataComponentType;
import dev.doublekekse.area_lib.registry.AreaDataComponentTypeRegistry;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class WabbajackAreaComponents {
    public static final AreaDataComponentType<WabbajackAreaComponent> WABBAJACK_AREA_COMPONENT = register("wabbajack_area", WabbajackAreaComponent::new);

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> WabbajackAreaCommand.register(dispatcher, registryAccess)
        );
    }

    private static <T extends AreaDataComponent> AreaDataComponentType<T> register(String name, Supplier<T> factory) {
        return AreaDataComponentTypeRegistry.registerTracking(Identifier.of(Wabbajack.MOD_ID, name), factory);
    }
}
