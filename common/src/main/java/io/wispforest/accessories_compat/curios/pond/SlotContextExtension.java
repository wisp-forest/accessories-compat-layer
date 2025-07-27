package io.wispforest.accessories_compat.curios.pond;

import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import top.theillusivec4.curios.api.SlotContext;

public interface SlotContextExtension {

    static SlotContext from(SlotReference slotReference) {
        var ctx = new SlotContext(
            CuriosConversionUtils.slotConvertSlotToC(slotReference.slotName()),
            slotReference.entity(),
            slotReference.slot(),
            false,
            true
        );

        var ext = (SlotContextExtension) (Object) ctx;

        ext.slotReference(slotReference);

        if (slotReference.entity() != null) {
            ext.isClient(slotReference.entity().level().isClientSide());
        }

        return ctx;
    }

    static SlotContextExtension from(SlotContext ctx) {
        return (SlotContextExtension) (Object) ctx;
    }

    void slotReference(SlotReference slotReference);

    SlotReference slotReference();
    default SlotType slotType() {
        return slotReference().type();
    }

    boolean isClient();
    void isClient(boolean value);
}
