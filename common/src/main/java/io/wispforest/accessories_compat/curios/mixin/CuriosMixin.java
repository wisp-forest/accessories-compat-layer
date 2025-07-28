package io.wispforest.accessories_compat.curios.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories_compat.curios.AccessoriesEventHooks;
import io.wispforest.accessories_compat.curios.mixin.accessor.CuriosImplMixinHooksAccessor;
import io.wispforest.accessories_compat.curios.wrapper.AccessoriesBasedCurioSlot;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;
import top.theillusivec4.curios.common.capability.ItemizedCurioCapability;
import top.theillusivec4.curios.server.SlotHelper;
import top.theillusivec4.curios.server.command.CurioArgumentType;

import java.util.HashSet;
import java.util.Set;

@Mixin(Curios.class)
public abstract class CuriosMixin {
    @Inject(method = "reload", at = @At("HEAD"), cancellable = true, remap = false)
    private void preventReload(AddReloadListenerEvent evt, CallbackInfo ci) {
        ci.cancel();
    }

    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/api/IEventBus;register(Ljava/lang/Object;)V", ordinal = 0), remap = false)
    private void preventRegistereingCuriosEventHandler(IEventBus instance, Object object, Operation<Void> original) {}

    @Inject(method = "setup", at = @At("TAIL"), remap = false)
    private void initEventHooks(CallbackInfo ci) {
        AccessoriesEventHooks.initAccessoriesEventHooks();
    }

    @WrapOperation(method = "lambda$registerCaps$1", at = @At(value = "NEW", target = "(Lnet/minecraft/world/entity/LivingEntity;)Ltop/theillusivec4/curios/common/capability/CurioInventoryCapability;"))
    private static CurioInventoryCapability useCuriosApiToCreateInvCap(LivingEntity livingEntity, Operation<CurioInventoryCapability> original) {
        var capability = AccessoriesCapability.get(livingEntity);
        return capability != null ? original.call(livingEntity) : null;
    }

    @WrapOperation(method = "registerCaps", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/capabilities/RegisterCapabilitiesEvent;registerItem(Lnet/neoforged/neoforge/capabilities/ItemCapability;Lnet/neoforged/neoforge/capabilities/ICapabilityProvider;[Lnet/minecraft/world/level/ItemLike;)V"))
    private <T, C> void registerProvidersAsAccessory(RegisterCapabilitiesEvent instance, ItemCapability<T, C> capability, ICapabilityProvider<ItemStack, C, T> provider, ItemLike[] items, Operation<Void> original) {
        if (CuriosConversionUtils.BASE_PROVIDER == null) {
            CuriosConversionUtils.BASE_PROVIDER = (ICapabilityProvider<ItemStack, Void, top.theillusivec4.curios.api.type.capability.ICurio>) provider;
        }

        for (var itemLike : items) {
            var item = itemLike.asItem();
            if (!CuriosImplMixinHooksAccessor.getCuriosRegistry().containsKey(item) && item instanceof ICurioItem itemCurio) {
                AccessoriesAPI.registerAccessory(item, CuriosConversionUtils.curioConvertToA((stack, ctx) -> {
                    return itemCurio.hasCurioCapability(stack)
                        ? new ItemizedCurioCapability(itemCurio, stack)
                        : null;
                }));
            }
        }

        original.call(instance, capability, CuriosConversionUtils.BASE_PROVIDER, items);
    }

    /**
     * @author blodhgarm
     * @reason easier to do this
     */
    @Overwrite(remap = false)
    private void serverAboutToStart(ServerAboutToStartEvent evt) {
        CuriosApi.setSlotHelper(new SlotHelper());
        Set<String> slotIds = new HashSet<>();

        SlotTypeLoader.INSTANCE.getSlotTypes(false).values().forEach(slotType -> {
            CuriosApi.getSlotHelper().addSlotType(new AccessoriesBasedCurioSlot(slotType));
            slotIds.add(CuriosConversionUtils.slotConvertToC(slotType.name()));
        });
        CurioArgumentType.slotIds = slotIds;
    }
}
