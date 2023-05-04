package com.scubakay.autorelog.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class Reconnect {
    private static ServerInfo server;
    private final static long DELAY = 1000 * 30;

    private static boolean active = false;

    public static void activate() {
        active = true;
    }

    public static void deactivate() {
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
                connect(MinecraftClient.getInstance(), ServerAddress.parse(server.address), server);
            }
        }, DELAY);
    }

    private static void connect(final MinecraftClient client, final ServerAddress address, @Nullable final ServerInfo info) {
        System.out.printf("Connecting to server: %s (%s)", server.name, server.address);

        Screen screen = client.currentScreen;
        InetSocketAddress inetSocketAddress = null;

        try {
            Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);

            if (optional.isEmpty()) {
                client.execute(() -> client.setScreen(new DisconnectedScreen(screen, ScreenTexts.CONNECT_FAILED, ConnectScreen.BLOCKED_HOST_TEXT)));
                return;
            }

            inetSocketAddress = optional.get();
            ClientConnection connection = ClientConnection.connect(inetSocketAddress, client.options.shouldUseNativeTransport());
            connection.setPacketListener(new ClientLoginNetworkHandler(connection, client, info, screen, false, null, Reconnect::setStatus));
            connection.send(new HandshakeC2SPacket(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), NetworkState.LOGIN));
            connection.send(new LoginHelloC2SPacket(client.getSession().getUsername(), Optional.ofNullable(client.getSession().getUuidOrNull())));
        } catch (Exception var6) {
            Throwable var5 = var6.getCause();
            Exception exception3;
            if (var5 instanceof Exception exception2) {
                exception3 = exception2;
            } else {
                exception3 = var6;
            }

            System.out.println("Couldn't connect to server");
            String string = inetSocketAddress == null ? exception3.getMessage() : exception3.getMessage().replaceAll(inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort(), "").replaceAll(inetSocketAddress.toString(), "");
            client.execute(() -> client.setScreen(new DisconnectedScreen(screen, ScreenTexts.CONNECT_FAILED, Text.translatable("disconnect.genericReason", string))));
        }
    }

    private static void setStatus(Text status) {

    }
}
