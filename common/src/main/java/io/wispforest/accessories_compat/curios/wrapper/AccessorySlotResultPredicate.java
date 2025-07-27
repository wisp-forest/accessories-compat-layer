package io.wispforest.accessories_compat.curios.wrapper;

import com.mojang.logging.LogUtils;
import io.wispforest.accessories.api.slot.SlotBasedPredicate;
import io.wispforest.accessories_compat.curios.pond.SlotContextExtension;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.SlotResult;

import java.util.function.Predicate;

public class AccessorySlotResultPredicate implements Predicate<SlotResult> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean hasErrored = false;

    private final ResourceLocation location;
    private final SlotBasedPredicate accessoryPredicate;

    public AccessorySlotResultPredicate(ResourceLocation location, SlotBasedPredicate accessoryPredicate) {
        this.location = location;
        this.accessoryPredicate = accessoryPredicate;
    }

    @Override
    public boolean test(SlotResult slotResult) {
        if(hasErrored) return false;

        var ctx = slotResult.slotContext();

        var entity = ctx.entity();
        var slotType = SlotContextExtension.from(ctx).slotType();

        try {
            return this.accessoryPredicate.isValid(entity != null ? entity.level() : null, slotType, ctx.index(), slotResult.stack()).orElse(false);
        } catch (Exception e) {
            this.hasErrored = true;
            LOGGER.warn("Unable to handle Curios Slot Predicate converted to Accessories Slot Predicate due to fundamental incompatibility, issues may be present with it! [Slot: {}, Predicate ID: {}]", slotType.name(), this.location, e);
        }

        return false;
    }
}
