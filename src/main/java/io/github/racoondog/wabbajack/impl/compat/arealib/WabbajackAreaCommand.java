package io.github.racoondog.wabbajack.impl.compat.arealib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.doublekekse.area_lib.Area;
import dev.doublekekse.area_lib.command.argument.AreaArgument;
import io.github.racoondog.wabbajack.impl.Wabbajack;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class WabbajackAreaCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("wabbajack-area").requires(source -> source.hasPermissionLevel(2)).then(argument("area", AreaArgument.area())
            .then(unit("disable-wabbajack", component -> component.disabled = true))
            .then(unit("enable-wabbajack", component -> component.disabled = false))
            .then(unit("disable-pvp", component -> component.pvp = TriState.FALSE))
            .then(unit("enable-pvp", component -> component.pvp = TriState.TRUE))

            .then(set("canBeWabbajacked", registryAccess, component -> component.canBeWabbajacked))
            .then(set("cannotBeWabbajacked", registryAccess, component -> component.cannotBeWabbajacked))

            .then(literal("reset").executes(ctx -> {
                AreaArgument.getArea(ctx, "area").remove(ctx.getSource().getServer(), WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT);
                return 1;
            }))
        ));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> unit(String parameter, Consumer<WabbajackAreaComponent> callback) {
        return literal(parameter).executes(ctx -> {
            Area area = AreaArgument.getArea(ctx, "area");
            WabbajackAreaComponent component = area.getOrDefault(WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT, new WabbajackAreaComponent());
            callback.accept(component);
            area.put(ctx.getSource().getServer(), WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT, component);
            ctx.getSource().sendMessage(Text.literal("Area successfully modified."));
            return 1;
        });
    }

    private static LiteralArgumentBuilder<ServerCommandSource> set(String parameter, CommandRegistryAccess registryAccess, Function<WabbajackAreaComponent, Set<EntityType<?>>> getter) {
        return literal(parameter)
            .then(literal("list").executes(ctx -> {
                Area area = AreaArgument.getArea(ctx, "area");
                @Nullable WabbajackAreaComponent component = area.get(WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT);
                MutableText text;
                if (component != null && !getter.apply(component).isEmpty()) {
                    text = Text.literal("This area adds the following mobs to ")
                        .append(highlight(parameter))
                        .append(":");

                    for (EntityType<?> entityType : getter.apply(component)) {
                        text.append(ScreenTexts.LINE_BREAK).append("- ").append(highlight(Registries.ENTITY_TYPE.getId(entityType).toString()));
                    }
                } else {
                    text = Text.literal("This area does not add any entities to ")
                        .append(highlight(parameter))
                        .append(".");
                }
                text.append(ScreenTexts.LINE_BREAK).append(
                    Text.literal("[View Defaults]").setStyle(Style.EMPTY.withFormatting(Formatting.GREEN).withClickEvent(
                        new ClickEvent.RunCommand("wabbajack list-wabbajackable-entities")
                    ))
                );
                ctx.getSource().sendMessage(text);
                return 1;
            }))
            .then(literal("add").then(argument("entityType", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE)).executes(ctx -> {
                Area area = AreaArgument.getArea(ctx, "area");
                WabbajackAreaComponent component = area.getOrDefault(WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT, new WabbajackAreaComponent());
                RegistryEntry<EntityType<?>> entityTypeEntry = RegistryEntryReferenceArgumentType.getEntityType(ctx, "entityType");
                if (getter.apply(component).add(entityTypeEntry.value())) {
                    area.put(ctx.getSource().getServer(), WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT, component);
                    ctx.getSource().sendMessage(Text.literal("Added ")
                        .append(highlight(entityTypeEntry.getIdAsString()))
                        .append(" to ")
                        .append(highlight(parameter))
                        .append("."));
                } else {
                    ctx.getSource().sendMessage(Text.literal("Nothing to add."));
                }
                return 1;
            })))
            .then(literal("remove").then(argument("entityType", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE)).executes(ctx -> {
                Area area = AreaArgument.getArea(ctx, "area");
                WabbajackAreaComponent component = area.getOrDefault(WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT, new WabbajackAreaComponent());
                RegistryEntry<EntityType<?>> entityTypeEntry = RegistryEntryReferenceArgumentType.getEntityType(ctx, "entityType");
                if (getter.apply(component).remove(entityTypeEntry.value())) {
                    area.put(ctx.getSource().getServer(), WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT, component);
                    ctx.getSource().sendMessage(Text.literal("Removed ")
                        .append(highlight(entityTypeEntry.getIdAsString()))
                        .append(" from ")
                        .append(highlight(parameter))
                        .append("."));
                } else {
                    ctx.getSource().sendMessage(Text.literal("Nothing to remove."));
                }
                return 1;
            })))
            .then(literal("clear").executes(ctx -> {
                Area area = AreaArgument.getArea(ctx, "area");
                @Nullable WabbajackAreaComponent component = area.get(WabbajackAreaComponents.WABBAJACK_AREA_COMPONENT);
                if (component != null && !getter.apply(component).isEmpty()) {
                    getter.apply(component).clear();
                    ctx.getSource().sendMessage(Text.literal("Cleared ")
                        .append(highlight(parameter))
                        .append("."));
                } else {
                    ctx.getSource().sendMessage(Text.literal("Nothing to clear."));
                }
                return 1;
            }));
    }

    private static MutableText highlight(String text) {
        return Text.literal(text).formatted(Formatting.GREEN);
    }
}
