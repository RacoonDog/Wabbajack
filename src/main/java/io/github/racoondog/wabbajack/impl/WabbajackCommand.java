package io.github.racoondog.wabbajack.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class WabbajackCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("wabbajack").requires(source -> source.hasPermissionLevel(2)).then(literal("list-wabbajackable-entities").executes(ctx -> {
            MutableText text = Text.literal("The following mobs can be")
                .append(Text.literal("Wabbajacked").formatted(Formatting.LIGHT_PURPLE))
                .append(":");

            for (RegistryEntry<EntityType<?>> entityType : Registries.ENTITY_TYPE.iterateEntries(DataTags.CAN_BE_WABBAJACKED)) {
                text.append(ScreenTexts.LINE_BREAK).append("- ").append(highlight(entityType.getIdAsString()));
            }

            ctx.getSource().sendMessage(text);
            return 1;
        })));
    }

    private static MutableText highlight(String text) {
        return Text.literal(text).formatted(Formatting.GREEN);
    }
}
