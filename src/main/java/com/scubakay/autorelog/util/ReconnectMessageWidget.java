package com.scubakay.autorelog.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ReconnectMessageWidget extends TextWidget {
    public ReconnectMessageWidget(TextRenderer textRenderer) {
        super(ReconnectMessageWidget.getDynamicText(), textRenderer);
    }

    private static Text getDynamicText() {
        Reconnect reconnect = Reconnect.getInstance();
        if (reconnect.isActive()) {
            if (!reconnect.isReconnecting()) {
                return Text.translatable("autorelog.disconnectedscreen.failed").formatted(Formatting.RED);
            } else if (reconnect.getCountdown() > 0) {
                return Text.translatable("autorelog.disconnectedscreen.countdown", reconnect.getCountdown()).formatted(Formatting.AQUA);
            } else {
                return Text.translatable("autorelog.disconnectedscreen.reconnecting").formatted(Formatting.DARK_GREEN);
            }
        }
        return Text.literal("");
    }

    @Override
    public Text getMessage() {
        return ReconnectMessageWidget.getDynamicText();
    }
}
