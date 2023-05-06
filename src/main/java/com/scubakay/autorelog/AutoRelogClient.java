package com.scubakay.autorelog;

import com.scubakay.autorelog.commands.AutoRelogCommand;
import com.scubakay.autorelog.util.Reconnect;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoRelogClient implements ClientModInitializer {
    public static final String MOD_ID = "autorelog";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register(Reconnect::register);
        ClientCommandRegistrationCallback.EVENT.register(AutoRelogCommand::register);
    }
}