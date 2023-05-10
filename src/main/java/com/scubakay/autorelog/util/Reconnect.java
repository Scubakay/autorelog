package com.scubakay.autorelog.util;

import com.scubakay.autorelog.AutoRelogClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class Reconnect {
    private static ServerInfo server;
    private final static long DELAY = 1000 * 30;
    private static Timer timer = new Timer();
    private static boolean active = false;
    private static ClientConnection connection;

    public static void activate() {
        AutoRelogClient.LOGGER.info("AutoRelog activated");
        active = true;
    }

    public static void deactivate() {
        AutoRelogClient.LOGGER.info("AutoRelog deactivated");
        timer.cancel();
        active = false;
    }

    public static void register(ClientPlayNetworkHandler handler, MinecraftClient ignoredClient) {
        if (active) {
            AutoRelogClient.LOGGER.info(String.format("Auto relogging in %d seconds", DELAY/1000));
            server = handler.getServerInfo();
            scheduleReconnect();
        }
    }

    public static void registerJoinEvent(ClientPlayNetworkHandler ignoredClientPlayNetworkHandler, PacketSender ignoredPacketSender, MinecraftClient ignoredMinecraftClient) {
        timer.cancel();
    }

    private static void scheduleReconnect() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connect(MinecraftClient.getInstance(), ServerAddress.parse(server.address), server);
            }
        }, DELAY, DELAY);
    }

    private static void connect(final MinecraftClient client, final ServerAddress address, @Nullable final ServerInfo info) {
        AutoRelogClient.LOGGER.info(String.format("Trying to connect to server: %s (%s)", server.name, server.address));

        Screen screen = client.currentScreen;
        InetSocketAddress inetSocketAddress;
        if (connection != null) {
            connection.disconnect(Text.literal("Disconnecting before trying again"));
        }

        try {
            Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);

            if (optional.isEmpty()) {
                AutoRelogClient.LOGGER.info("Couldn't resolve server address");
                return;
            }

            inetSocketAddress = optional.get();
            connection = ClientConnection.connect(inetSocketAddress, client.options.shouldUseNativeTransport());
            connection.setPacketListener(new ClientLoginNetworkHandler(connection, client, info, screen, false, null, Reconnect::setStatus));
            connection.send(new HandshakeC2SPacket(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), NetworkState.LOGIN));
            connection.send(new LoginHelloC2SPacket(client.getSession().getUsername(), Optional.ofNullable(client.getSession().getUuidOrNull())));
        } catch (Exception exception) {
            AutoRelogClient.LOGGER.info(String.format("Couldn't connect to server: %s", exception.getCause()));
        }
    }

    private static void setStatus(Text status) {
        AutoRelogClient.LOGGER.info(status.getString());
    }
}
