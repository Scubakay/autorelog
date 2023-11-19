package com.scubakay.autorelog.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.scubakay.autorelog.AutoRelogClient;
import com.scubakay.autorelog.commands.suggestions.DelaySuggestionProvider;
import com.scubakay.autorelog.commands.suggestions.IntervalSuggestionProvider;
import com.scubakay.autorelog.commands.suggestions.MaxAttemptsSuggestionProvider;
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
                .build();

        LiteralCommandNode<FabricClientCommandSource> configNode = ClientCommandManager
                .literal("config")
                .build();

        ArgumentCommandNode<FabricClientCommandSource, Integer> delayArgumentNode = ClientCommandManager
                .argument("delay", IntegerArgumentType.integer())
                .suggests(new DelaySuggestionProvider())
                .executes(ctx -> delay(ctx, IntegerArgumentType.getInteger(ctx, "delay")))
                .build();

        LiteralCommandNode<FabricClientCommandSource> intervalNode = ClientCommandManager
                .literal("interval")
                .build();

        ArgumentCommandNode<FabricClientCommandSource, Integer> intervalArgumentNode = ClientCommandManager
                .argument("interval", IntegerArgumentType.integer())
                .suggests(new IntervalSuggestionProvider())
                .executes(ctx -> interval(ctx, IntegerArgumentType.getInteger(ctx, "interval")))
                .build();

        LiteralCommandNode<FabricClientCommandSource> maxAttemptsNode = ClientCommandManager
                .literal("maxAttempts")
                .build();

        ArgumentCommandNode<FabricClientCommandSource, Integer> maxAttemptsArgumentNode = ClientCommandManager
                .argument("maxAttempts", IntegerArgumentType.integer())
                .suggests(new MaxAttemptsSuggestionProvider())
                .executes(ctx -> maxAttempts(ctx, IntegerArgumentType.getInteger(ctx, "maxAttempts")))
                .build();

        LiteralCommandNode<FabricClientCommandSource> cancelNode = ClientCommandManager
                .literal("cancel")
                .executes(AutoRelogCommand::cancel)
                .build();

        dispatcher.getRoot().addChild(autoRelogNode);

        // Add config node
        autoRelogNode.addChild(configNode);

        // Add delay config node
        configNode.addChild(delayNode);
        delayNode.addChild(delayArgumentNode);

        // Add delay config node
        configNode.addChild(intervalNode);
        intervalNode.addChild(intervalArgumentNode);

        // Add max attempts config node
        configNode.addChild(maxAttemptsNode);
        maxAttemptsNode.addChild(maxAttemptsArgumentNode);

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

    private static int maxAttempts(CommandContext<FabricClientCommandSource> context, int maxAttempts) {
        AutoRelogClient.CONFIG.setMaxAttempts(maxAttempts);
        context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_max_attempts_changed", maxAttempts), false);
        return 1;
    }
}
