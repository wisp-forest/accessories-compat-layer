package io.wispforest.accessories_compat.curios.mixin.accessories;

import io.wispforest.accessories.impl.AccessoriesCapabilityImpl;
import io.wispforest.accessories_compat.curios.pond.CurioInventoryCapabilityExtension;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AccessoriesCapabilityImpl.class)
public abstract class AccessoriesCapabilityImplMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void attemptCurioConversion(LivingEntity entity, CallbackInfo ci) {
        CurioInventoryCapabilityExtension.attemptConversion((AccessoriesCapabilityImpl) (Object) this);
    }
}
