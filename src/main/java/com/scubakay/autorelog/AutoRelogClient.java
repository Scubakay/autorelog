package com.scubakay.autorelog;

import com.scubakay.autorelog.commands.AutoRelogCommand;
import com.scubakay.autorelog.util.Reconnect;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class AutoRelogClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register(Reconnect::register);
        ClientCommandRegistrationCallback.EVENT.register(AutoRelogCommand::register);
    }
}