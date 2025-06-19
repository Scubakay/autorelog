package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? < 1.20 {
/*import net.minecraft.client.gui.widget.ButtonWidget;
*///?}

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
    //? >= 1.21.6 {
    @Inject(method = "disconnect", at = @At("HEAD"))
    private static void injectDisconnect(CallbackInfo ci) {
    //?} else if >= 1.20 {
    /*@Inject(method = "disconnect", at = @At("HEAD"))
    private void injectDisconnect(CallbackInfo ci) {
    *///?} else {
    /*@Inject(method = "method_19836", at = @At("HEAD"))
    private void injectDisconnect(ButtonWidget button, CallbackInfo ci) {
    *///?}
        Reconnect.getInstance().deactivate();
    }

}
