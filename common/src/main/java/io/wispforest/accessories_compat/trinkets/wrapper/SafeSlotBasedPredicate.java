package io.wispforest.accessories_compat.trinkets.wrapper;

import com.mojang.datafixers.util.Function3;
import com.mojang.logging.LogUtils;
import dev.emi.trinkets.api.SlotReference;
import io.wispforest.accessories.api.slot.EntityBasedPredicate;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class SafeSlotBasedPredicate implements EntityBasedPredicate {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean hasErrored = false;

    private final ResourceLocation location;
    private final Function3<ItemStack, SlotReference, LivingEntity, TriState> trinketPredicate;

    public SafeSlotBasedPredicate(ResourceLocation location, Function3<ItemStack, SlotReference, LivingEntity, TriState> trinketPredicate) {
        this.location = location;
        this.trinketPredicate = trinketPredicate;
    }

    @Override
    public TriState isValid(Level level, @Nullable LivingEntity entity, io.wispforest.accessories.api.slot.SlotType slotType, int slot, ItemStack stack) {
        if(hasErrored) return TriState.DEFAULT;

        try {
            return this.trinketPredicate.apply(stack, new SlotReference(new EmptyTrinketInventory(entity, slotType, level.isClientSide()), slot), entity);
        } catch (Exception e) {
            this.hasErrored = true;
            LOGGER.warn("Unable to handle Trinket Slot Predicate converted to Accessories Slot Predicate due to fundamental incompatibility, issues may be present with it! [Slot: {}, Predicate ID: {}]", slotType.name(), this.location, e);
        }

        return TriState.DEFAULT;
    }
}
