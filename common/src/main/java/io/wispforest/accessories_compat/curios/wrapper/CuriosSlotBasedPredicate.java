package io.wispforest.accessories_compat.curios.wrapper;

import com.mojang.logging.LogUtils;
import io.wispforest.accessories.api.slot.SlotBasedPredicate;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import java.util.function.Predicate;

public class CuriosSlotBasedPredicate implements SlotBasedPredicate {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean hasErrored = false;

    private final ResourceLocation location;
    private final Predicate<SlotResult> curiosValidator;

    public CuriosSlotBasedPredicate(ResourceLocation location, Predicate<SlotResult> curiosValidator) {
        this.location = location;
        this.curiosValidator = curiosValidator;
    }

    @Override
    public TriState isValid(Level level, io.wispforest.accessories.api.slot.SlotType slotType, int slot, ItemStack stack) {
        if(hasErrored) return TriState.DEFAULT;

        try {
            return TriState.of(this.curiosValidator.test(new SlotResult(new SlotContext(slotType.name(), null, slot, false, true), stack)));
        } catch (Exception e) {
            this.hasErrored = true;
            LOGGER.warn("Unable to handle Curios Slot Predicate converted to Accessories Slot Predicate due to fundamental incompatibility, issues may be present with it! [Slot: {}, Predicate ID: {}]", slotType.name(), this.location, e);
        }

        return TriState.DEFAULT;
    }
}
