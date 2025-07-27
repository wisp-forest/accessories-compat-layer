package io.wispforest.accessories_compat.curios.mixin.accessories;

import io.wispforest.accessories.api.attributes.SlotAttribute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// Mixin attempts to patch slot attribute to be instead of instance equality into equality on the slot name
@Mixin(SlotAttribute.class)
public abstract class SlotAttributeMixin {

    @Shadow @Final private String slotName;

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;
        if (!(obj instanceof SlotAttribute otherAttribute)) return false;

        return this.slotName.equals(otherAttribute.slotName());
    }
}
