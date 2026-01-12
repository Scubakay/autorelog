package com.scubakay.autorelog.util;

import com.scubakay.autorelog.AutoRelogClient;
import com.scubakay.autorelog.config.Config;
import lombok.Getter;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.*;

//? >= 1.20.3 {
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
//?} else {
/*import net.minecraft.client.gui.screen.ConnectScreen;
*///?}

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Reconnect {
    @Getter private boolean active = false;
    @Getter private boolean reconnecting = false;
    @Getter private int countdown = -1;

    private ServerInfo server;
    private ServerAddress address;
    private Timer timer;

    private int attemptsLeft = 0;

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

    public boolean hasAddress() {
        return address != null;
    }

    public void activate() {
        if (!active) {
            if (Config.logging == Logging.ENABLED) AutoRelogClient.LOGGER.info("AutoRelog activated");
            active = true;
        }
    }

    public void deactivate() {
        if (active) {
            if (Config.logging == Logging.ENABLED) AutoRelogClient.LOGGER.info("AutoRelog deactivated");
            timer.cancel();
            active = false;
            reconnecting = false;
            countdown = -1;
        }
    }

    public void startReconnecting() {
        if (active && !reconnecting) {
            attemptsLeft = Config.maxAttempts;
            if (Config.logging == Logging.ENABLED)
                AutoRelogClient.LOGGER.info("Auto relogging every {} seconds", Config.interval);
            scheduleReconnect();
            reconnecting = true;
        } else if (active && Config.maxAttempts > 0) {
            attemptsLeft--;
            if (attemptsLeft == 0) {
                if (Config.logging == Logging.ENABLED)
                    AutoRelogClient.LOGGER.info("Failed all reconnection attempts, stopping...");
                reconnecting = false;
                countdown = -1;
            } else {
                if (Config.logging == Logging.ENABLED)
                    AutoRelogClient.LOGGER.info("Failed to connect. Trying again in {} seconds with {} attempts left.", Config.interval, attemptsLeft);
                this.scheduleReconnect();
            }
        } else if (active) {
            if (Config.logging == Logging.ENABLED)
                AutoRelogClient.LOGGER.info("Trying again in {} seconds.", Config.interval);
            this.scheduleReconnect();
        }
    }

    public void join(ClientPlayNetworkHandler handler) {
        getServerAddress(handler);
        if (active) {
            reconnecting = false;
            if (Config.logging == Logging.ENABLED) AutoRelogClient.LOGGER.info("Relogged to server successfully!");
            timer.cancel();
        }
    }

    private void getServerAddress(ClientPlayNetworkHandler handler) {
        //? >= 1.19.3 {
            server = handler.getServerInfo();
        //?} else {
            /*server = MinecraftClient.getInstance().getCurrentServerEntry();
        *///?}

        address = server == null ? null : ServerAddress.parse(server.address);
    }

    private void scheduleReconnect() {
        // Don't try to reconnect when we don't have an address
        if (address == null) return;

        if (timer != null) {
            timer = new Timer();
        }
        countdown = Config.interval;
        Objects.requireNonNull(timer).schedule(new TimerTask() {
            @Override
            public void run() {
                if (countdown <= 0) {
                    MinecraftClient.getInstance().execute(Reconnect.getInstance()::connect);
                }
                countdown--;
            }
        }, 0, 1000);
    }

    public void connect() {
        if (Config.logging == Logging.ENABLED) AutoRelogClient.LOGGER.info("Trying to reconnect...");
        if (attemptsLeft <= 0) {
            deactivate();
        }
        ConnectScreen.connect(
                new MultiplayerScreen(new TitleScreen()),
                MinecraftClient.getInstance(),
                address,
                server
                /*? >=1.20 {*/, false/*?}*/
                /*? >=1.20.5 {*/, null/*?}*/
        );
        timer.cancel();
    }
}
