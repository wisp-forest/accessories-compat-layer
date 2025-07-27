package io.wispforest.accessories_compat.curios.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.common.capability.ItemizedCurioCapability;

@Mixin(ItemizedCurioCapability.class)
public interface ItemizedCurioCapabilityAccessor {
    @Accessor(value = "curioItem", remap = false)
    ICurioItem getCurioItem();
}
