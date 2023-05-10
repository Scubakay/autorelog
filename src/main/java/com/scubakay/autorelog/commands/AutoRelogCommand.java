package com.scubakay.autorelog.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
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

        LiteralCommandNode<FabricClientCommandSource> cancelNode = ClientCommandManager
                .literal("cancel")
                .executes(AutoRelogCommand::cancel)
                .build();

        dispatcher.getRoot().addChild(autoRelogNode);
        autoRelogNode.addChild(cancelNode);
    }

    private static int autorelog(CommandContext<FabricClientCommandSource> context) {
        if (context.getSource().getClient().isInSingleplayer()) {
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_error_singleplayer"), false);
            return 1;
        }
        Reconnect.activate();
        context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_activated"), false);
        return 1;
    }

    private static int cancel(CommandContext<FabricClientCommandSource> context) {
        if (context.getSource().getClient().isInSingleplayer()) {
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_error_singleplayer"), false);
            return 1;
        }
        Reconnect.deactivate();
        context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_deactivated"), false);
        return 1;
    }
}
