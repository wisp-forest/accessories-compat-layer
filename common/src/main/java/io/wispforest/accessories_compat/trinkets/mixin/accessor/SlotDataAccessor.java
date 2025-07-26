package io.wispforest.accessories_compat.trinkets.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(
    targets = "dev/emi/trinkets/data/SlotLoader$SlotData"
    /*value = SlotLoader.SlotData*/
)
public interface SlotDataAccessor {
    @Accessor(value = "order", remap = false) int getOrder();
    @Accessor(value = "amount", remap = false) int getAmount();
    @Accessor(value = "icon", remap = false) String getIcon();
    @Accessor(value = "quickMovePredicates", remap = false) Set<String> getQuickMovePredicates();
    @Accessor(value = "validatorPredicates", remap = false) Set<String> getValidatorPredicates();
    @Accessor(value = "tooltipPredicates", remap = false) Set<String> getTooltipPredicates();
    @Accessor(value = "dropRule", remap = false) String getDropRule();
}
