package io.wispforest.accessories_compat.trinkets.mixin.client;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPacketListener.class, priority = 1500)
public abstract class ClientPlayNetworkHandlerMixin {

    @TargetHandler(
        mixin = "dev.emi.trinkets.mixin.ClientPlayNetworkHandlerMixin",
        name = "onPlayerRespawn"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void onPlayerRespawn(CallbackInfo ci)  {
        ci.cancel();
    }
}
