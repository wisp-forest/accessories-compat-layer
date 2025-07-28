package io.wispforest.accessories_compat.curios.mixin;

import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories_compat.curios.pond.SlotContextExtension;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.theillusivec4.curios.api.SlotContext;

@Mixin(SlotContext.class)
public abstract class SlotContextMixin implements SlotContextExtension {

    @Shadow @Final private String identifier;
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
    public SlotType slotType() {
        if (slotReference != null) return slotReference().type();

        var slot = SlotTypeLoader.INSTANCE.getSlotTypes(isClient).get(CuriosConversionUtils.slotConvertToA(this.identifier));

        if (slot == null) {
            throw new IllegalStateException("Unable to get the slotType from slotReference or from converting the curios id to accessories! Id: " + this.identifier);
        }

        return slot;
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
