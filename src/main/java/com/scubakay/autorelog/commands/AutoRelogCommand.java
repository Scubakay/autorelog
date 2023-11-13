package com.scubakay.autorelog.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.scubakay.autorelog.AutoRelogClient;
import com.scubakay.autorelog.commands.suggestions.DelaySuggestionProvider;
import com.scubakay.autorelog.commands.suggestions.IntervalSuggestionProvider;
import com.scubakay.autorelog.util.Reconnect;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class AutoRelogCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess ignoredCommandRegistryAccess) {
        LiteralCommandNode<FabricClientCommandSource> autoRelogNode = ClientCommandManager
                .literal("autorelog")
                .executes(AutoRelogCommand::autorelog)
                .build();

        LiteralCommandNode<FabricClientCommandSource> delayNode = ClientCommandManager
                .literal("delay")
                .executes(AutoRelogCommand::autorelog)
                .build();

        ArgumentCommandNode<FabricClientCommandSource, Integer> delayArgumentNode = ClientCommandManager
                .argument("delay", IntegerArgumentType.integer())
                .suggests(new DelaySuggestionProvider())
                .executes(ctx -> delay(ctx, IntegerArgumentType.getInteger(ctx, "delay")))
                .build();

        LiteralCommandNode<FabricClientCommandSource> intervalNode = ClientCommandManager
                .literal("interval")
                .executes(AutoRelogCommand::autorelog)
                .build();

        ArgumentCommandNode<FabricClientCommandSource, Integer> intervalArgumentNode = ClientCommandManager
                .argument("interval", IntegerArgumentType.integer())
                .suggests(new IntervalSuggestionProvider())
                .executes(ctx -> interval(ctx, IntegerArgumentType.getInteger(ctx, "interval")))
                .build();

        LiteralCommandNode<FabricClientCommandSource> cancelNode = ClientCommandManager
                .literal("cancel")
                .executes(AutoRelogCommand::cancel)
                .build();

        dispatcher.getRoot().addChild(autoRelogNode);

        // Add delay config node
        autoRelogNode.addChild(delayNode);
        delayNode.addChild(delayArgumentNode);

        // Add delay config node
        autoRelogNode.addChild(intervalNode);
        intervalNode.addChild(intervalArgumentNode);

        autoRelogNode.addChild(cancelNode);
    }

    private static int autorelog(CommandContext<FabricClientCommandSource> context) {
        if (context.getSource().getClient().isInSingleplayer()) {
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_error_singleplayer"), false);
            return -1;
        }
        Reconnect.getInstance().activate();
        context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_activated"), false);
        return 1;
    }

    private static int cancel(CommandContext<FabricClientCommandSource> context) {
        if (context.getSource().getClient().isInSingleplayer()) {
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_error_singleplayer"), false);
            return -1;
        }
        Reconnect.getInstance().deactivate();
        context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_deactivated"), false);
        return 1;
    }

    private static int delay(CommandContext<FabricClientCommandSource> context, int delay) {
        AutoRelogClient.CONFIG.setDelay(delay);
        context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_delay_changed", delay), false);
        return 1;
    }

    private static int interval(CommandContext<FabricClientCommandSource> context, int interval) {
        AutoRelogClient.CONFIG.setInterval(interval);
        context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_interval_changed", interval), false);
        return 1;
    }
}
