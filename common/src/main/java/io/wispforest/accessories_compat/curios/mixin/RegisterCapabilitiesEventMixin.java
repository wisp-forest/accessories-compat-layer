package io.wispforest.accessories_compat.curios.mixin;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Objects;

@Mixin(RegisterCapabilitiesEvent.class)
public abstract class RegisterCapabilitiesEventMixin {
    @Inject(method = "registerItem", at = @At("HEAD"))
    private <T, C> void hookForBypassingCuriosRegisterCall(ItemCapability<T, C> capability, ICapabilityProvider<ItemStack, C, T> provider, ItemLike[] items, CallbackInfo ci) {
        if(capability.equals(CuriosCapability.ITEM) && !provider.equals(CuriosConversionUtils.BASE_PROVIDER)){
            var wrappedCurio = CuriosConversionUtils.curioConvertToA((ICapabilityProvider<ItemStack, Void, ICurio>) (Object) provider);

            for (var itemLike : items) AccessoriesAPI.registerAccessory(Objects.requireNonNull(itemLike.asItem()), wrappedCurio);
        }
    }
}
