package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;
import com.scubakay.autorelog.util.ReconnectButtonWidget;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? >= 1.20.2 {
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import org.spongepowered.asm.mixin.Final;
//?} else if >= 1.20 {
/*import net.minecraft.client.gui.widget.GridWidget;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
*///?} else {
/*import org.spongepowered.asm.mixin.injection.Redirect;
*///?}

@SuppressWarnings("UnusedMixin")
@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen {
    //? >= 1.20.2 {
    @Final
    @Shadow
    private DirectionalLayoutWidget grid;
    //?} else if >= 1.20 {
    /*@Final
    @Shadow
    private GridWidget grid;
    *///?} else {
    /*@Shadow
    private int reasonHeight;
    *///?}

    protected DisconnectedScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void autoRelog$injectInit(CallbackInfo ci) {
        Reconnect.getInstance().startReconnecting();
    }

    //? >= 1.20.2 {
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private void autoRelog$injectReconnectButton(CallbackInfo ci) {
    //?} else if >= 1.20 {
    /*@Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private void autoRelog$injectReconnectButton(CallbackInfo ci, @Local GridWidget.Adder adder) {
    *///?} else {
    /*@Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DisconnectedScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    private void autoRelog$injectReconnectButton(CallbackInfo ci) {
    *///?}
        if (Reconnect.getInstance().hasAddress()) {
            ButtonWidget widget = new ReconnectButtonWidget();
            //? >=1.20.2 {
            this.grid.add(widget);
            //?} else if >= 1.20 {
            /*adder.add(widget);
            *///?} else if >= 1.19.3 {
            /*widget.setX(this.width / 2 - (widget.getWidth() / 2));
            widget.setY(Math.min(this.height / 2 + this.reasonHeight / 2 + 9, this.height - 30));
            this.addDrawableChild(widget);
            *///?} else {
            /*widget.x = this.width / 2 - (widget.getWidth() / 2);
            widget.y = Math.min(this.height / 2 + this.reasonHeight / 2 + 9, this.height - 30);
            this.addDrawableChild(widget);
            *///?}
        }
    }


    //? < 1.20 {
    /*/^
     * Correct the location of the existing buttons in the disconnect screen for 1.19.x and lower
     ^/
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int autoRelog$correctButtonYLevel(int a, int b) {
        int autorelogButtonHeight = 24;
        return Math.min(this.height / 2 + this.reasonHeight / 2 + 9 + autorelogButtonHeight, b);
    }
    *///?}
}