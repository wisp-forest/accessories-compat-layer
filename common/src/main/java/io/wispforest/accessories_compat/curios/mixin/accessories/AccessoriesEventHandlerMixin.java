package io.wispforest.accessories_compat.curios.mixin.accessories;

import io.wispforest.accessories.impl.AccessoriesEventHandler;
import io.wispforest.accessories_compat.curios.AccessoriesEventHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(AccessoriesEventHandler.class)
public class AccessoriesEventHandlerMixin {
    @Inject(method = "onDeath",  at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getGameRules()Lnet/minecraft/world/level/GameRules;"))
    private static void cclayer$setupStackChecks(LivingEntity entity, DamageSource source, CallbackInfoReturnable<Collection<ItemStack>> cir) {
        AccessoriesEventHooks.DeathWrapperEventsImpl.INSTANCE.collectDropRules(entity, source);
    }

    @Inject(method = "onDeath",  at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/util/TriState;orElse(Z)Z"))
    private static void cclayer$clearDropRules(LivingEntity entity, DamageSource source, CallbackInfoReturnable<Collection<ItemStack>> cir) {
        AccessoriesEventHooks.DeathWrapperEventsImpl.INSTANCE.removeDropRules();
    }
}
