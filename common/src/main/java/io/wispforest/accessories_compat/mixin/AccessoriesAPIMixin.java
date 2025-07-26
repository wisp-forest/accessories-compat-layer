package io.wispforest.accessories_compat.mixin;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AccessoriesAPI.class)
public abstract class AccessoriesAPIMixin {
    @Inject(method = "getAttributeModifiers(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Ljava/lang/String;IZ)Lio/wispforest/accessories/api/attributes/AccessoryAttributeBuilder;", at = @At("RETURN"))
    private static void trinkets$getDataAttributeModifiers(ItemStack stack, @Nullable LivingEntity entity, String slotName, int slot, boolean hideTooltipIfDisabled, CallbackInfoReturnable<AccessoryAttributeBuilder> cir) {
        var builder = cir.getReturnValue();

        for (var module : ModCompatibilityModule.getModules().values()) {
            module.getAttributes(stack, entity, slotName, slot, builder);
        }
    }
}
