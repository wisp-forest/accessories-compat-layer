package io.wispforest.accessories_compat.trinkets.mixin;

import dev.emi.trinkets.TrinketScreenManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrinketScreenManager.class, priority = 2000)
public abstract class TrinketScreenManagerMixin {

    @Inject(method = {
        "drawGroup",
        "drawActiveGroup",
        "drawExtraGroups"
    }, at = @At("HEAD"), cancellable = true, remap = false)
    private static void accessories$preventMethodInvocation1(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = {
        "init",
        "removeSelections",
        "update",
        "tick"
    }, at = @At("HEAD"), cancellable = true, remap = false)
    private static void accessories$preventMethodInvocation2(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "isClickInsideTrinketBounds", at = @At("HEAD"), cancellable = true, remap = false)
    private static void accessories$preventIsClickInsideTrinketBounds(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
