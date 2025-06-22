package com.scubakay.autorelog.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ReconnectButtonWidget extends ButtonWidget {
    private final Reconnect reconnect;

    public static ReconnectButtonWidgetBuilder builder() {
        return new ReconnectButtonWidgetBuilder(
                (button) -> MinecraftClient.getInstance().execute(Reconnect.getInstance()::connect)
        );
    }

    protected ReconnectButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
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

    public static class ReconnectButtonWidgetBuilder extends ButtonWidget.Builder {
        public ReconnectButtonWidgetBuilder(PressAction onPress) {
            super(Text.empty(), onPress);
        }

        public ButtonWidget build() {
            ButtonWidget buttonWidget = new ReconnectButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }
}
