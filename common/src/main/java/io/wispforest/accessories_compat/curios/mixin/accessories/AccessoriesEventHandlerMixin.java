package io.wispforest.accessories_compat.curios.mixin.accessories;

import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories.impl.AccessoriesEventHandler;
import io.wispforest.accessories_compat.AccessoriesCompatInit;
import io.wispforest.accessories_compat.curios.AccessoriesEventHooks;
import io.wispforest.accessories_compat.curios.CuriosCompat;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(AccessoriesEventHandler.class)
public abstract class AccessoriesEventHandlerMixin {
    @Inject(method = "onDeath",  at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getGameRules()Lnet/minecraft/world/level/GameRules;"))
    private static void cclayer$setupStackChecks(LivingEntity entity, DamageSource source, CallbackInfoReturnable<Collection<ItemStack>> cir) {
        AccessoriesEventHooks.DeathWrapperEventsImpl.INSTANCE.collectDropRules(entity, source);
    }

    @Inject(method = "onDeath",  at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/util/TriState;orElse(Z)Z"))
    private static void cclayer$clearDropRules(LivingEntity entity, DamageSource source, CallbackInfoReturnable<Collection<ItemStack>> cir) {
        AccessoriesEventHooks.DeathWrapperEventsImpl.INSTANCE.removeDropRules();
    }

    @Inject(method = "addEntityBasedTooltipData", at = @At(value = "INVOKE", target = "Ljava/util/HashSet;containsAll(Ljava/util/Collection;)Z"))
    private static void cclayer$adjustTooltipForPocket(
        LivingEntity entity, Accessory accessory, ItemStack stack, List<Component> tooltip, Item.TooltipContext tooltipContext, TooltipFlag tooltipType, CallbackInfo ci,
        @Local(name = "validSlotTypes") HashSet<SlotType> validSlotTypes, @Local(name = "sharedSlotTypes") Set<SlotType> sharedSlotTypes
    ) {
        var type = SlotTypeLoader.getSlotType(entity, "pocket");

        if (type == null || AccessoriesCompatInit.CONFIG.showPocketSlotInTooltip()) return;

        if (validSlotTypes.size() > 1 && !AccessoriesAPI.getPredicateResults(CuriosCompat.filterValidators(type), entity.level(), type, 0, stack)) {
            validSlotTypes.remove(type);
            sharedSlotTypes.remove(type);
        }
    }
}
