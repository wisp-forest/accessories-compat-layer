package io.wispforest.accessories_compat.mixin;

import io.wispforest.accessories.Accessories;
import io.wispforest.accessories_compat.AccessoriesCompatInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Accessories.class, remap = false)
public class AccessoriesMixin {
    @Inject(method = "init", at = @At("HEAD"), remap = false)
    private static void loadAccessoriesCompatBefore(CallbackInfo ci) {
        AccessoriesCompatInit.init();
    }
}
