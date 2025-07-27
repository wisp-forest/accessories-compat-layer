package io.wispforest.accessories_compat.trinkets.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.emi.trinkets.api.TrinketItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TrinketItem.class)
public abstract class TrinketItemMixin {
    @WrapMethod(method = "equipItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Z")
    private static <T> boolean adjustMethodToAccessories(LivingEntity user, ItemStack stack, Operation<Boolean> original) {
        var capability = AccessoriesCapability.get(user);

        if (capability != null) return capability.attemptToEquipAccessory(stack) != null;

        return false;
    }
}
