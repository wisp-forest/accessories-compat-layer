package io.wispforest.accessories_compat.trinkets.pond;

import io.wispforest.accessories.api.slot.SlotType;

import java.util.Map;

public interface SlotGroupExtension {

    void accessories$setWrapperData(boolean isClientSide, Map<String, SlotType> slots);

    io.wispforest.accessories.api.slot.SlotGroup accessories$slotGroup();

    Map<String, SlotType> accessories$slots();

    boolean isWrapped();
}
