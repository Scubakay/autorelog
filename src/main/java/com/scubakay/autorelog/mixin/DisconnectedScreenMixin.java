package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.util.Reconnect;
import com.scubakay.autorelog.util.ReconnectButtonWidget;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? >= 1.20.2 {
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
//?} else {
/*import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import com.llamalad7.mixinextras.sugar.Local;
*///?}

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen {
    @Final
    @Shadow
    //? >= 1.20.2 {
    private DirectionalLayoutWidget grid;
    //?} else {
    /*private GridWidget grid;
    *///?}

    protected DisconnectedScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void injectInit(CallbackInfo ci) {
        Reconnect.getInstance().startReconnecting();
    }

    //? >= 1.20.2 {
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private void injectReconnectButton(CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private void injectReconnectButton(CallbackInfo ci, @Local GridWidget.Adder adder) {
    *///?}
        if (Reconnect.getInstance().hasAddress()) {
            ButtonWidget widget = ReconnectButtonWidget.builder().width(200).build();
            /*? >=1.20.2 {*/this.grid/*?} else {*//*adder*//*?}*/.add(widget);
        }
    }
}
