package io.wispforest.accessories_compat.mixin;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketsWrappingUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Accessory.class)
public interface AccessoryMixin {
    @Inject(method = "canEquipFromUse(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void tclayer$checkIfFromTrinket(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        for (var value : ModCompatibilityModule.getModules().values()) {
            if (stack.is(TagKey.create(Registries.ITEM, value.getAllItemTag())) && AccessoriesAPI.isDefaultAccessory((Accessory)(Object)this)) {
                cir.setReturnValue(false);

                break;
            }
        }
    }
}
