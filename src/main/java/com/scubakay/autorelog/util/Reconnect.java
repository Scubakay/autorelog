package com.scubakay.autorelog.util;

import com.scubakay.autorelog.AutoRelogClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.*;

import java.util.Timer;
import java.util.TimerTask;

public class Reconnect {
    private ServerInfo server;
    private ServerAddress address;
    private final static long DELAY = 1000 * 30;
    private Timer timer = new Timer();
    private boolean active = false;

    private static Reconnect instance;
    public static Reconnect getInstance() {
        if (instance == null) {
            instance = new Reconnect();
        }
        return instance;
    }

    public static void registerJoinEvent(ClientPlayNetworkHandler handler, PacketSender ignoredPacketSender, MinecraftClient ignoredMinecraftClient) {
        Reconnect.getInstance().join(handler);
    }

    public void activate() {
        AutoRelogClient.LOGGER.info("AutoRelog activated");
        active = true;
    }

    public void deactivate() {
        AutoRelogClient.LOGGER.info("AutoRelog deactivated");
        timer.cancel();
        active = false;
    }

    public void startReconnecting() {
        if (active) {
            AutoRelogClient.LOGGER.info(String.format("Auto relogging in %d seconds", DELAY/1000));
            scheduleReconnect();
        }
    }

    public void join(ClientPlayNetworkHandler handler) {
        server = handler.getServerInfo();
        address = ServerAddress.parse(server.address);
        timer.cancel();
    }

    private void scheduleReconnect() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MinecraftClient.getInstance().execute(Reconnect.getInstance()::connect);
            }
        }, DELAY, DELAY);
    }

    public void connect() {
        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), MinecraftClient.getInstance(), address, server, false);
    }
}
