package io.wispforest.accessories_compat.trinkets.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(
    targets = "dev/emi/trinkets/data/SlotLoader$GroupData"
    /*value = SlotLoader.SlotData*/
)
public interface GroupDataAccessor {
    @Accessor(value = "slotId", remap = false) int getSlotId();
    @Accessor(value = "order", remap = false) int getOrder();
    @Accessor(value = "slots", remap = false) Map<String, SlotDataAccessor> getSlots();
}
