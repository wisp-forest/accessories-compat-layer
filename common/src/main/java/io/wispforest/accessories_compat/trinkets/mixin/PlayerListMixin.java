package io.wispforest.accessories_compat.trinkets.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, priority = 1500)
public abstract class PlayerListMixin {

    @TargetHandler(
        mixin = "dev.emi.trinkets.mixin.PlayerManagerMixin",
        name = "onPlayerConnect"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void preventOnPlayerConnect(CallbackInfo ci) {
        ci.cancel();
    }
}
