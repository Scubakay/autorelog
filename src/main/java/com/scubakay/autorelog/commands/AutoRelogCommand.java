package com.scubakay.autorelog.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.scubakay.autorelog.util.Reconnect;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class AutoRelogCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess ignoredCommandRegistryAccess) {
        dispatcher.register(ClientCommandManager.literal("autorelog").executes(AutoRelogCommand::run));
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        if (context.getSource().getClient().isInSingleplayer()) {
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_error_singleplayer"), false);
            return 1;
        }
        Reconnect.activate();
        context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_activated"), false);
        return 1;
    }
}
