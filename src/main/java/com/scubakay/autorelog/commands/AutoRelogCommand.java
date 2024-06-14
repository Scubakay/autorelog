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

        dispatcher.getRoot().addChild(autoRelogNode);
    }

    private static int autorelog(CommandContext<FabricClientCommandSource> context) {
        if (context.getSource().getClient().isInSingleplayer()) {
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_error_singleplayer"), false);
            return -1;
        }
        if (Reconnect.getInstance().isActive()) {
            Reconnect.getInstance().deactivate();
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_deactivated"), false);
        } else {
            Reconnect.getInstance().activate();
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_activated"), false);
        }
        return 1;
    }
}
