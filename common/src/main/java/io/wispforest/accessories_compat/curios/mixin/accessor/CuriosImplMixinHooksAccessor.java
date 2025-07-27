package io.wispforest.accessories_compat.curios.mixin.accessor;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.mixin.CuriosImplMixinHooks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(CuriosImplMixinHooks.class)
public interface CuriosImplMixinHooksAccessor {

    @Accessor("REGISTRY")
    static Map<Item, ICurioItem> getCuriosRegistry() {
        return null;
    }
}
