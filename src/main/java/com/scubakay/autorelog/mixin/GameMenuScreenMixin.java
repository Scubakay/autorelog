package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? < 1.19.3 {
/*import net.minecraft.client.gui.widget.ButtonWidget;
*///?}

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
    //? >= 1.19.3 {
    @Inject(method = "disconnect", at = @At("HEAD"))
    private void injectDisconnect(CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "method_19836", at = @At("HEAD"))
    private void injectDisconnect(ButtonWidget button, CallbackInfo ci) {
    *///?}
        Reconnect.getInstance().deactivate();
    }
}
