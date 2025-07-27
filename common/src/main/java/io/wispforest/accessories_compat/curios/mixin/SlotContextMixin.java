package io.wispforest.accessories_compat.curios.mixin;

import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories_compat.curios.pond.SlotContextExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.theillusivec4.curios.api.SlotContext;

@Mixin(SlotContext.class)
public abstract class SlotContextMixin implements SlotContextExtension {

    @Unique
    private SlotReference slotReference;

    @Unique
    private boolean isClient = false;

    @Override
    public void slotReference(SlotReference slotReference) {
        this.slotReference = slotReference;
    }

    @Override
    public SlotReference slotReference() {
        return slotReference;
    }

    @Override
    public boolean isClient() {
        return isClient;
    }

    @Override
    public void isClient(boolean value) {
        isClient = value;
    }
}
