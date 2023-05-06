package com.scubakay.autorelog.util;

import com.scubakay.autorelog.AutoRelogClient;
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
    private final static long PERIOD = 1000 * 5;

    private static boolean active = false;

    public static void activate() {
        active = true;
    }

    public static void deactivate() {
        AutoRelogClient.LOGGER.info("AutoRelog deactivated");
        active = false;
    }

    public static void register(ClientPlayNetworkHandler handler, MinecraftClient ignoredClient) {
        if (active) {
            server = handler.getServerInfo();
            scheduleReconnect();
        }
    }

    private static void scheduleReconnect() {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (connect(MinecraftClient.getInstance(), ServerAddress.parse(server.address), server)) {
                    cancel();
                }
            }
        }, DELAY, PERIOD);
    }

    private static boolean connect(final MinecraftClient client, final ServerAddress address, @Nullable final ServerInfo info) {
        AutoRelogClient.LOGGER.info(String.format("Connecting to server: %s (%s)", server.name, server.address));

        Screen screen = client.currentScreen;
        InetSocketAddress inetSocketAddress;

        try {
            Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);

            if (optional.isEmpty()) {
                return false;
            }

            inetSocketAddress = optional.get();
            ClientConnection connection = ClientConnection.connect(inetSocketAddress, client.options.shouldUseNativeTransport());
            connection.setPacketListener(new ClientLoginNetworkHandler(connection, client, info, screen, false, null, Reconnect::setStatus));
            connection.send(new HandshakeC2SPacket(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), NetworkState.LOGIN));
            connection.send(new LoginHelloC2SPacket(client.getSession().getUsername(), Optional.ofNullable(client.getSession().getUuidOrNull())));
            return true;
        } catch (Exception var6) {
            AutoRelogClient.LOGGER.info("Couldn't connect to server");
            return false;
        }
    }

    private static void setStatus(Text status) {
        AutoRelogClient.LOGGER.info("Reconnected to server!");
    }
}
