package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Inject(method = "init", at = @At("HEAD"))
    private void injectInit(CallbackInfo ci) {
        Reconnect.deactivate();
    }
}
