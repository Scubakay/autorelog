package com.scubakay.autorelog.mixin;

import com.scubakay.autorelog.AutoRelogClient;
import com.scubakay.autorelog.util.Reconnect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow
    public Input input;

    @Unique
    private long lastInput = 0;

    //initializing with Reconnect::isActive required for afk detection to work properly after a relog
    @Unique
    private boolean isAfk = Reconnect.getInstance().isActive() && AutoRelogClient.CONFIG.isAfkDetection();

    @Inject(method = "tick", at = @At("HEAD"))
    private void detectInput(CallbackInfo ci) {
        if(lastInput == 0) lastInput = System.currentTimeMillis();
        if (!AutoRelogClient.CONFIG.isAfkDetection()) return;
        checkIsAfk(input.getMovementInput());
    }

    @Unique
    private void checkIsAfk(Vec2f movementVec) {
        if (movementVec.equals(Vec2f.ZERO)) {
            if (isAfk) return;
            if (System.currentTimeMillis() - lastInput < AutoRelogClient.CONFIG.getAfkDelay() * 1000L) return;
            isAfk = true;
            Reconnect.getInstance().activate();
            return;
        }
        lastInput = System.currentTimeMillis();
        if (!isAfk) return;
        isAfk = false;
        Reconnect.getInstance().deactivate();
    }
}
