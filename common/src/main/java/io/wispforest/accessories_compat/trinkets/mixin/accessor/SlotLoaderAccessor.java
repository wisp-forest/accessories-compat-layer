package io.wispforest.accessories_compat.trinkets.mixin.accessor;

import dev.emi.trinkets.data.SlotLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SlotLoader.class)
public interface SlotLoaderAccessor {
    @Accessor(value = "slots", remap = false)
    Map<String, GroupDataAccessor> getLoadedSlots();
}
