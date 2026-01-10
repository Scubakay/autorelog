package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.injection.Redirect;

//? >= 1.20.6 {
import net.minecraft.text.Text;
//?} else {
/*import net.minecraft.client.gui.screen.Screen;
*///?}

@SuppressWarnings("UnusedMixin")
@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
    //? >= 1.20.6 {
    @Redirect(
            method = "method_72129",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;disconnect(Lnet/minecraft/text/Text;)V"
            )
    )
    private void redirectDisconnect(MinecraftClient client, Text reason) {
    //?} else {
    /*@Redirect(
            method = "method_19836",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;disconnect(Lnet/minecraft/client/gui/screen/Screen;)V"
            )
    )
    private void redirectDisconnect(MinecraftClient client, Screen reason) {
    *///?}
        Reconnect.getInstance().deactivate();
        client.disconnect(reason);
    }
}
