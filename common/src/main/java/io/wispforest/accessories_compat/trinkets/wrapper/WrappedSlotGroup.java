package io.wispforest.accessories_compat.trinkets.wrapper;

import dev.emi.trinkets.api.SlotGroup;
import io.wispforest.accessories_compat.trinkets.mixin.SlotGroupMixin;
import io.wispforest.accessories_compat.trinkets.pond.SlotGroupExtension;

import java.util.Map;

public class WrappedSlotGroup {

    public static SlotGroup of(String group, Map<String, io.wispforest.accessories.api.slot.SlotType> slots, boolean isClientSide) {
        return new ExtendedSlotGroupBuilder(group, slots, isClientSide).build();
    }

    public static class ExtendedSlotGroupBuilder extends SlotGroup.Builder {

        private final Map<String, io.wispforest.accessories.api.slot.SlotType> accessorySlots;
        private final boolean isClientSide;

        public ExtendedSlotGroupBuilder(String name, Map<String, io.wispforest.accessories.api.slot.SlotType> slots, boolean isClientSide) {
            super(name, 0, 0);

            ((SlotGroupMixin.SlotGroupBuilderAccessor) this).slots(TrinketsWrappingUtils.slotType(slots, name));

            this.accessorySlots = slots;
            this.isClientSide = isClientSide;
        }

        @Override
        public SlotGroup build() {
            var group = super.build();

            ((SlotGroupExtension) (Object) group).accessories$setWrapperData(isClientSide, accessorySlots);

            return group;
        }
    }
}
