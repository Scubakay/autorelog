package com.scubakay.autorelog.commands;

import com.mojang.brigadier.context.CommandContext;
import com.scubakay.autorelog.util.Reconnect;
import de.maxhenkel.admiral.annotations.Command;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class AutoRelogCommand {
    @Command("autorelog")
    public void autorelog(CommandContext<FabricClientCommandSource> context) {
        if (context.getSource().getClient().isInSingleplayer()) {
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_error_singleplayer"), false);
        } else if (Reconnect.getInstance().isActive()) {
            Reconnect.getInstance().deactivate();
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_deactivated"), false);
        } else {
            Reconnect.getInstance().activate();
            context.getSource().getPlayer().sendMessage(Text.translatable("commands.autorelog_activated"), false);
        }
    }
}
