package io.wispforest.accessories_compat.mixin;

import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.api.ReloadListenerRegisterCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Pseudo
@Mixin(targets = "io/wispforest/accessories/neoforge/AccessoriesForge", remap = false)
public abstract class AccessoriesForgeMixin {

    @Inject(method = "intermediateRegisterListeners", at = @At("HEAD"))
    private void registerAdditionalResourceLoaders(Consumer<PreparableReloadListener> registrationMethod, CallbackInfo ci) {
        var callback = new ReloadListenerRegisterCallback() {
            @Override
            public void registerSlotLoader(PreparableReloadListener listener, ResourceLocation id) {
                registrationMethod.accept(listener);
            }

            @Override
            public void registerEntitySlotLoader(PreparableReloadListener listener, ResourceLocation id) {
                registrationMethod.accept(listener);
            }
        };

        for (var value : ModCompatibilityModule.getModules().values()) {
            value.registerDataLoaders(callback);
        }
    }
}
