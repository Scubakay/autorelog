package com.scubakay.autorelog.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

//? >=1.19.3 {
import java.util.function.Supplier;
//?}

public class ReconnectButtonWidget extends ButtonWidget {
    private final Reconnect reconnect;

    public ReconnectButtonWidget() {
        super(0, 0, 200, 20, Text.empty(), (button) -> MinecraftClient.getInstance().execute(Reconnect.getInstance()::connect) /*? >=1.19.3 {*/, Supplier::get/*?}*/);
        reconnect = Reconnect.getInstance();
    }

    @Override
    public Text getMessage() {
        active = true;
        Text message;
        if (!reconnect.isActive() || (hovered && !(reconnect.isReconnecting() && reconnect.getCountdown() <= 0))) {
            message =  Text.translatable("autorelog.disconnectedscreen.reconnectnow").formatted(Formatting.DARK_GREEN);
        } else if (!reconnect.isReconnecting()) {
            message = Text.translatable("autorelog.disconnectedscreen.failed").formatted(Formatting.RED);
        } else if (reconnect.getCountdown() > 0) {
            message = Text.translatable("autorelog.disconnectedscreen.countdown", reconnect.getCountdown()).formatted(Formatting.AQUA);
        } else {
            active = false;
            message =  Text.translatable("autorelog.disconnectedscreen.reconnecting").formatted(Formatting.DARK_GREEN);
        }
        return message;
    }
}
