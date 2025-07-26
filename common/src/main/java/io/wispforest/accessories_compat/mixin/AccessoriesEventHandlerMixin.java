package io.wispforest.accessories_compat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.impl.AccessoriesEventHandler;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AccessoriesEventHandler.class)
public abstract class AccessoriesEventHandlerMixin {

    @WrapOperation(
            method = "handleInvalidStacks",
            at = @At(value = "INVOKE", target = "Lio/wispforest/accessories/api/AccessoriesAPI;canInsertIntoSlot(Lnet/minecraft/world/item/ItemStack;Lio/wispforest/accessories/api/slot/SlotReference;)Z"))
    private static boolean tclayer$adjustCheckBehavior(ItemStack stack, SlotReference reference, Operation<Boolean> operation){
        var accessory = AccessoriesAPI.getAccessory(stack);

        for (var value : ModCompatibilityModule.getModules().values()) {
            if (value.skipOnEquipCheck(stack, accessory)) {
                var slotType = reference.type();

                return AccessoriesAPI.getPredicateResults(slotType.validators(), reference.entity().level(), reference.entity(), slotType, 0, stack);
            }
        }

        return operation.call(stack, reference);
    }
}
