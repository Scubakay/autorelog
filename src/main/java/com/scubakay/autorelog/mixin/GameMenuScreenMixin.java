package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.widget.ButtonWidget;

@SuppressWarnings("UnusedMixin")
@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
    @Inject(
            method = "method_19836",
            at = @At("HEAD")
    )
    private void autoRelog$disconnect(ButtonWidget button, CallbackInfo ci) {
        Reconnect.getInstance().deactivate();
    }
}
