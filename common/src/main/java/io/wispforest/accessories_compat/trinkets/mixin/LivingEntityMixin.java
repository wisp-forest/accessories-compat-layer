package io.wispforest.accessories_compat.trinkets.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("CancellableInjectionUsage")
@Mixin(value = LivingEntity.class, priority = 1500)
public abstract class LivingEntityMixin {

    @TargetHandler(
        mixin = "dev.emi.trinkets.mixin.LivingEntityMixin",
        name = "canFreeze"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void preventCanFreeze(CallbackInfo ci) {
        ci.cancel();
    }

    @TargetHandler(
        mixin = "dev.emi.trinkets.mixin.LivingEntityMixin",
        name = "dropInventory"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void preventDropInventory(CallbackInfo info, CallbackInfo ci) {
        ci.cancel();
    }

    @TargetHandler(
        mixin = "dev.emi.trinkets.mixin.LivingEntityMixin",
        name = "tick"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void preventTick(CallbackInfo info, CallbackInfo ci) {
        ci.cancel();
    }
}
