package com.scubakay.autorelog.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Formatting;

//? >=1.19.3 {
import java.util.function.Supplier;
//?}

public class ReconnectButtonWidget extends ButtonWidget/*? >=1.21.11 {*/.Text/*?}*/ {
    private final Reconnect reconnect;

    public ReconnectButtonWidget() {
        super(0, 0, 200, 20, net.minecraft.text.Text.empty(), (button) -> MinecraftClient.getInstance().execute(Reconnect.getInstance()::connect) /*? >=1.19.3 {*/, Supplier::get/*?}*/);
        reconnect = Reconnect.getInstance();
    }

    @Override
    public net.minecraft.text.Text getMessage() {
        active = true;
        net.minecraft.text.Text message;
        if (!reconnect.isActive() || (hovered && !(reconnect.isReconnecting() && reconnect.getCountdown() <= 0))) {
            message =  net.minecraft.text.Text.translatable("autorelog.disconnectedscreen.reconnectnow").formatted(Formatting.DARK_GREEN);
        } else if (!reconnect.isReconnecting()) {
            message = net.minecraft.text.Text.translatable("autorelog.disconnectedscreen.failed").formatted(Formatting.RED);
        } else if (reconnect.getCountdown() > 0) {
            message = net.minecraft.text.Text.translatable("autorelog.disconnectedscreen.countdown", reconnect.getCountdown()).formatted(Formatting.AQUA);
        } else {
            active = false;
            message =  net.minecraft.text.Text.translatable("autorelog.disconnectedscreen.reconnecting").formatted(Formatting.DARK_GREEN);
        }
        return message;
    }
}
