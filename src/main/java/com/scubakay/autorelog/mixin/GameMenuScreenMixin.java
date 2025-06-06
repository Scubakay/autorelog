//~ gamemenuscreenmixin_import
//~ gamemenuscreenmixin_inject
//~ gamemenuscreenmixin_signature
package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    private void injectDisconnect(CallbackInfo ci) {
        Reconnect.getInstance().deactivate();
    }
}
