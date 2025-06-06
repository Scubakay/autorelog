package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;
import com.scubakay.autorelog.util.ReconnectMessageWidget;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen {
    @Final
    @Shadow
    private DirectionalLayoutWidget grid;

    protected DisconnectedScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void injectInit(CallbackInfo ci) {
        Reconnect.getInstance().startReconnecting();
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 1))
    private void injectButtonWidget(CallbackInfo ci) {
        if (Reconnect.getInstance().isActive()) {
            this.grid.add(new ReconnectMessageWidget(this.textRenderer));
        }
    }
}
