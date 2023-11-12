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
    private final static long DELAY = 1000L * AutoRelogClient.CONFIG.getDelay();
    private final static long INTERVAL = 1000L * AutoRelogClient.CONFIG.getInterval();
    private Timer timer;
    private boolean active = false;
    private boolean reconnecting = false;

    public static void registerJoinEvent(ClientPlayNetworkHandler handler, PacketSender ignoredPacketSender, MinecraftClient ignoredMinecraftClient) {
        Reconnect.getInstance().join(handler);
    }

    private static Reconnect instance;
    public static Reconnect getInstance() {
        if (instance == null) {
            instance = new Reconnect();
        }
        return instance;
    }

    private Reconnect() {
        timer = new Timer();
    }

    public void activate() {
        if (!active) {
            AutoRelogClient.LOGGER.info("AutoRelog activated");
            active = true;
        }
    }

    public void deactivate() {
        if (active) {
            AutoRelogClient.LOGGER.info("AutoRelog deactivated");
            timer.cancel();
            active = false;
        }
    }

    public void startReconnecting() {
        if (active && !reconnecting) {
            AutoRelogClient.LOGGER.info(String.format("Auto relogging every %d seconds in %d seconds", AutoRelogClient.CONFIG.getInterval(), AutoRelogClient.CONFIG.getDelay()));
            scheduleReconnect();
            reconnecting = true;
        }
    }

    public void join(ClientPlayNetworkHandler handler) {
        server = handler.getServerInfo();
        address = ServerAddress.parse(server.address);
        if(active) {
            reconnecting = false;
            AutoRelogClient.LOGGER.info("Relogged to server successfully!");
            timer.cancel();
        }
    }

    private void scheduleReconnect() {
        if (timer != null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                AutoRelogClient.LOGGER.info("Trying to reconnect...");
                MinecraftClient.getInstance().execute(Reconnect.getInstance()::connect);
            }
        }, DELAY, INTERVAL);
    }

    public void connect() {
        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), MinecraftClient.getInstance(), address, server, false);
    }
}
