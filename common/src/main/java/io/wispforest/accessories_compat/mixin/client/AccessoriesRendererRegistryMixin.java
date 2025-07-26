package io.wispforest.accessories_compat.mixin.client;

import com.google.common.collect.BiMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AccessoriesRendererRegistry.class)
public abstract class AccessoriesRendererRegistryMixin {
    @WrapOperation(method = "getRenderer(Lnet/minecraft/world/item/Item;)Lio/wispforest/accessories/api/client/AccessoryRenderer;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/BiMap;containsKey(Ljava/lang/Object;)Z", remap = false))
    private static boolean alterDefaultBehavior(BiMap map, Object key, Operation<Boolean> operation, @Local(argsOnly = true) Item item) {
        for (var value : ModCompatibilityModule.getModules().values()) {
            if (value.skipDefaultRenderer(item)) {
                return true;
            }
        }

        return operation.call(map, key);
    }
}
