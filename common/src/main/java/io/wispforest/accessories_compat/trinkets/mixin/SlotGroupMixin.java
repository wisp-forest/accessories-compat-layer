package io.wispforest.accessories_compat.trinkets.mixin;

import dev.emi.trinkets.api.SlotGroup;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.data.SlotGroupLoader;
import io.wispforest.accessories_compat.trinkets.pond.SlotGroupBuilderExtension;
import io.wispforest.accessories_compat.trinkets.pond.SlotGroupExtension;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketsWrappingUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Objects;

@Mixin(SlotGroup.class)
public abstract class SlotGroupMixin implements SlotGroupExtension {

    @Shadow(remap = false) public abstract String getName();

    @Unique
    private io.wispforest.accessories.api.slot.SlotGroup slotGroup = null;
    @Unique
    private Map<String, io.wispforest.accessories.api.slot.SlotType> slots = null;

    @Override
    public void accessories$setWrapperData(boolean isClientSide, Map<String, SlotType> slots) {
        var group = this.getName();

        this.slots = slots;
        this.slotGroup = SlotGroupLoader.INSTANCE.getGroup(isClientSide, TrinketsWrappingUtils.trinketsToAccessories_Group(group));

        Objects.requireNonNull(this.slotGroup, "WrappedSlotGroup: Unable to locate the following Group [" + group + "]");
    }

    @Override
    public io.wispforest.accessories.api.slot.SlotGroup accessories$slotGroup() {
        return slotGroup;
    }

    @Override
    public Map<String, SlotType> accessories$slots() {
        return slots;
    }

    @Override
    public boolean isWrapped() {
        return slotGroup != null && slots != null;
    }

    @Override
    public int hashCode() {
        return (isWrapped()) ? accessories$slotGroup().hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (isWrapped()) {
            if (obj instanceof SlotGroupExtension otherGroup && otherGroup.isWrapped()) {
                return otherGroup.accessories$slotGroup().equals(this.accessories$slotGroup());
            }

            return false;
        }

        return super.equals(obj);
    }

    @Mixin(SlotGroup.Builder.class)
    public static abstract class SlotGroupBuilderMixin implements SlotGroupBuilderExtension {

        @Shadow(remap = false)
        @Mutable
        private Map<String, dev.emi.trinkets.api.SlotType> slots;

        @Override
        public void accessories$slots(Map<String, dev.emi.trinkets.api.SlotType> slots) {
            this.slots = slots;
        }
    }
}
