package io.wispforest.accessories_compat.curios.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories_compat.curios.pond.SlotContextExtension;
import io.wispforest.accessories_compat.curios.wrapper.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.EntityCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.mixin.CuriosImplMixinHooks;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(CuriosImplMixinHooks.class)
public abstract class CuriosImplMixinHooksMixin {
    @Shadow
    private static Map<String, ISlotType> filteredSlots(Predicate<ISlotType> filter, Map<String, ISlotType> map) {
        return null;
    }

    @Inject(method = "registerCurio", at = @At("TAIL"))
    private static void registerCurioAsAccessory(Item item, ICurioItem icurio, CallbackInfo ci) {
        AccessoriesAPI.registerAccessory(item, CuriosConversionUtils.curioConvertToA(icurio));
    }

    @Inject(method = "getCurioFromRegistry", at = @At("HEAD"), cancellable = true)
    private static void getAccessoryAsCurio(Item item, CallbackInfoReturnable<Optional<ICurioItem>> cir) {
        var accessory = AccessoriesAPI.getAccessory(item);

        if (accessory != null && !(accessory instanceof AccessoryFromCurio)) cir.setReturnValue(Optional.of(new CurioFromAccessory(accessory)));
    }

    /**
     * @author blodhgarm
     * @reason must get more data into lamda call
     */
    @Overwrite
    public static Map<String, ISlotType> getItemStackSlots(ItemStack stack, boolean isClient) {
        return filteredSlots(slotType -> {
            SlotContext slotContext = new SlotContext(slotType.getIdentifier(), null, 0, false, true);
            SlotContextExtension.from(slotContext).isClient(isClient);
            SlotResult slotResult = new SlotResult(slotContext, stack);
            return CuriosApi.testCurioPredicates(slotType.getValidators(), slotResult);
        }, CuriosApi.getSlots(isClient));
    }

    @Inject(method = "getCurio", at = @At("HEAD"), cancellable = true)
    private static void getAccessoryAsCurio(ItemStack stack, CallbackInfoReturnable<Optional<ICurio>> cir) {
        var accessory = AccessoriesAPI.getAccessory(stack);

        if (accessory != null) cir.setReturnValue(Optional.ofNullable(CuriosConversionUtils.accessoryConvertToC(accessory, stack)));
    }

    @WrapOperation(method = "getCuriosInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getCapability(Lnet/neoforged/neoforge/capabilities/EntityCapability;)Ljava/lang/Object;"))
    private static Object checkForAccessoriesCapability(LivingEntity instance, EntityCapability entityCapability, Operation<Object> original) {
        var capability = AccessoriesCapability.get(instance);

        return capability != null ? original.call(instance, entityCapability) : null;
    }

    @Inject(method = "broadcastCurioBreakEvent", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/network/PacketDistributor;sendToPlayersTrackingEntityAndSelf(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;[Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V"))
    private static void broadcastAsAccessoryEvent(SlotContext slotContext, CallbackInfo ci) {
        AccessoriesAPI.breakStack(CuriosConversionUtils.convertToA(slotContext));
        ci.cancel();
    }

    @WrapOperation(method = "getSlotId", at = @At(value = "INVOKE", target = "Ltop/theillusivec4/curios/api/SlotContext;identifier()Ljava/lang/String;"))
    private static String filterForBadChars(SlotContext instance, Operation<String> original) {
        return original.call(instance).replace(":", "-");
    }

    @Inject(method = "registerCurioPredicate", at = @At("TAIL"))
    private static void registerPredicateAsAccessoryPredicate(ResourceLocation resourceLocation, Predicate<SlotResult> validator, CallbackInfo ci) {
        if(resourceLocation.getNamespace().equals(CuriosApi.MODID)) return;

        AccessoriesAPI.registerPredicate(resourceLocation, new CuriosSlotBasedPredicate(resourceLocation, validator));
    }

    /**
     * @author blodharm
     * @reason get accessory predicate instead
     */
    @Overwrite
    public static Optional<Predicate<SlotResult>> getCurioPredicate(ResourceLocation resourceLocation) {
        var location = CuriosConversionUtils.convertToA(resourceLocation);
        var predicate = AccessoriesAPI.getPredicate(location);

        return Optional.of(new AccessorySlotResultPredicate(location, predicate));
    }

    /**
     * @author blodharm
     * @reason test using accessories API
     */
    @Overwrite(remap = false)
    public static boolean testCurioPredicates(Set<ResourceLocation> predicates, SlotResult slotResult) {
        predicates = predicates.stream().map(CuriosConversionUtils::convertToA).collect(Collectors.toSet());

        var ctx = slotResult.slotContext();

        LivingEntity livingEntity = ctx.entity();
        Level level = livingEntity != null ? livingEntity.level() : null;

        SlotType slotType = SlotContextExtension.from(ctx).slotType();

        try {
            return AccessoriesAPI.getPredicateResults(predicates, level, livingEntity, slotType, ctx.index(), slotResult.stack());
        } catch (Exception ignored) {}

        return false;
    }
}
