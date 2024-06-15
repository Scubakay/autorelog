package com.scubakay.autorelog.util;

import com.scubakay.autorelog.AutoRelogClient;
import com.scubakay.autorelog.config.Config;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.*;

import java.util.Timer;
import java.util.TimerTask;

public class Reconnect {
    private ServerInfo server;
    private ServerAddress address;
    private Timer timer;
    private boolean active = false;
    private boolean reconnecting = false;
    private int attemptsLeft = 0;
    private int countdown = -1;

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

    public boolean isActive() {
        return active;
    }

    public int getCountdown() {
        return countdown;
    }

    public void activate() {
        if (!active) {
            if(Config.logging == Logging.ENABLED) AutoRelogClient.LOGGER.info("AutoRelog activated");
            active = true;
        }
    }

    public void deactivate() {
        if (active) {
            if(Config.logging == Logging.ENABLED) AutoRelogClient.LOGGER.info("AutoRelog deactivated");
            timer.cancel();
            active = false;
            reconnecting = false;
            countdown = -1;
        }
    }

    public void startReconnecting() {
        if (active && !reconnecting) {
            attemptsLeft = Config.maxAttempts;
            if(Config.logging == Logging.ENABLED) AutoRelogClient.LOGGER.info(String.format("Auto relogging every %d seconds", Config.interval));
            scheduleReconnect();
            reconnecting = true;
        } else if(active && Config.maxAttempts > 0) {
            attemptsLeft--;
            if (attemptsLeft == 0) {
                if (Config.logging == Logging.ENABLED)
                    AutoRelogClient.LOGGER.info("Failed all reconnection attempts, stopping...");
                deactivate();
            } else {
                if (Config.logging == Logging.ENABLED)
                    AutoRelogClient.LOGGER.info(String.format("Failed to connect. Trying again in %d seconds with %d attempts left.", Config.interval, attemptsLeft));
                this.scheduleReconnect();
            }
        } else if(active) {
            if (Config.logging == Logging.ENABLED)
                AutoRelogClient.LOGGER.info(String.format("Failed to connect. Trying again in %d seconds.", Config.interval));
            this.scheduleReconnect();
        }
    }

    public void join(ClientPlayNetworkHandler handler) {
        server = handler.getServerInfo();
        if (server != null) {
            // If server is null this is single player, so don't parse it.
            address = ServerAddress.parse(server.address);
        }
        if(active) {
            reconnecting = false;
            if(Config.logging == Logging.ENABLED) AutoRelogClient.LOGGER.info("Relogged to server successfully!");
            timer.cancel();
        }
    }

    private void scheduleReconnect() {
        // Don't try to reconnect when we don't have an address
        if(address == null) return;

        if (timer != null) {
            timer = new Timer();
        }
        countdown = Config.interval;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                countdown--;
                if (countdown <= 0) {
                    MinecraftClient.getInstance().execute(Reconnect.getInstance()::connect);
                }
            }
        }, 0, 1000);
    }

    public void connect() {
        if(Config.logging == Logging.ENABLED) AutoRelogClient.LOGGER.info("Trying to reconnect...");

        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), MinecraftClient.getInstance(), address, server, false, null);
        timer.cancel();
    }
}
