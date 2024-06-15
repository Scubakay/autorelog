package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow protected TextRenderer textRenderer;

    @Inject(method = "render", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void injectCountdown(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (((Screen)(Object)this) instanceof DisconnectedScreen) {
            if(Reconnect.getInstance().isActive()) {
                int countdown = Reconnect.getInstance().getCountdown();
                if (!Reconnect.getInstance().isReconnecting()) {
                    context.drawTextWithShadow(this.textRenderer, Text.translatable("autorelog.disconnectedscreen.failed"), 5, 5, 0xFF0000);
                } else if (countdown > 0) {
                    context.drawTextWithShadow(this.textRenderer, Text.translatable("autorelog.disconnectedscreen.countdown", countdown), 5, 5, 0xFF0000);
                } else if (countdown <= 0) {
                    context.drawTextWithShadow(this.textRenderer, Text.translatable("autorelog.disconnectedscreen.reconnecting"), 5, 5, 0xFF0000);
                }
            }
        }
    }
}
