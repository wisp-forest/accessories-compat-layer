package io.wispforest.accessories_compat.trinkets.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemStack.class, priority = 1500)
public abstract class ItemStackMixin {

    @TargetHandler(
        mixin = "dev.emi.trinkets.mixin.ItemStackMixin",
        name = "getTooltip"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void preventGetTooltip(CallbackInfo ci) {
        ci.cancel();
    }
}
