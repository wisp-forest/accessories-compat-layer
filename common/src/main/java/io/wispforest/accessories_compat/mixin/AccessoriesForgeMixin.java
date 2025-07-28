package io.wispforest.accessories_compat.mixin;

import io.wispforest.accessories.neoforge.AccessoriesForge;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.utils.ReloadListenerRegisterCallbackImpl;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

//@Debug(export = true)
//@Pseudo
@Mixin(
    value = AccessoriesForge.class,
//    targets = "io/wispforest/accessories/neoforge/AccessoriesForge",
    remap = false
)
public abstract class AccessoriesForgeMixin {

    @Unique
    private final ReloadListenerRegisterCallbackImpl listeners = new ReloadListenerRegisterCallbackImpl();

    @Inject(method = "intermediateRegisterListeners", at = @At("HEAD"))
    private void registerAdditionalResourceLoaders(Consumer<PreparableReloadListener> registrationMethod, CallbackInfo ci) {
        for (var value : ModCompatibilityModule.getModules().values()) {
            value.registerDataLoaders(listeners);
        }
    }

    @Inject(method = "intermediateRegisterListeners", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 0, remap = false))
    private void registerLoadersAtCorrectTimeForSlot(Consumer<PreparableReloadListener> registrationMethod, CallbackInfo ci) {
        var beforeSlotListeners = listeners.beforeSlotListeners();

        beforeSlotListeners.forEach(registrationMethod);
        beforeSlotListeners.clear();
    }

    @Inject(method = "intermediateRegisterListeners", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", ordinal = 1, remap = false))
    private <T> void registerLoadersAtCorrectTimeForEntity(Consumer<PreparableReloadListener> registrationMethod, CallbackInfo ci) {
        var beforeEntityListeners = listeners.beforeEntityListeners();

        beforeEntityListeners.forEach(registrationMethod);
        beforeEntityListeners.clear();
    }
}