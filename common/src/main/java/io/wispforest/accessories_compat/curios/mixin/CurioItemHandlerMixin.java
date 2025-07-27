package io.wispforest.accessories_compat.curios.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.capability.CurioInventory;
import top.theillusivec4.curios.common.capability.CurioItemHandler;

import java.util.Map;

@Mixin(CurioItemHandler.class)
public abstract class CurioItemHandlerMixin {
    @WrapOperation(method = "<init>", at = @At(value = "FIELD", target = "Ltop/theillusivec4/curios/common/capability/CurioInventory;curios:Ljava/util/Map;"))
    private Map<String, ICurioStacksHandler> adjustMapGrabToMethod(CurioInventory instance, Operation<Map<String, ICurioStacksHandler>> original) {
        return instance.asMap();
    }
}
