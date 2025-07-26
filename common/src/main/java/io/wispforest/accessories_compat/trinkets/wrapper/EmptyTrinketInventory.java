package io.wispforest.accessories_compat.trinkets.wrapper;

import dev.emi.trinkets.api.TrinketInventory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class EmptyTrinketInventory extends TrinketInventory {
    public EmptyTrinketInventory(@Nullable LivingEntity entity, io.wispforest.accessories.api.slot.SlotType slotType, boolean isClientSide) {
        super(WrappedSlotType.of(slotType, isClientSide), new EmptyTrinketComponent(entity), inv -> {});
    }
}
