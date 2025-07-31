package io.wispforest.accessories_compat.mixin;

import io.wispforest.accessories.DataLoaderBase;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.api.ReloadListenerRegisterCallback;
import io.wispforest.accessories_compat.utils.ModifiableIdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Pseudo
@Mixin(value = DataLoaderBase.class, remap = false)
public abstract class DataLoaderBaseMixin {

    @Shadow(remap = false)
    protected abstract Optional<PreparableReloadListener> getIdentifiedSlotLoader();

    @Shadow(remap = false)
    protected abstract Optional<PreparableReloadListener> getIdentifiedEntitySlotLoader();

    @Inject(method = "registerListeners", at = @At("HEAD"), cancellable = true)
    private void adjustDependenciesAndAddListeners(CallbackInfo ci){
        var manager = ResourceManagerHelper.get(PackType.SERVER_DATA);

        var slotLoader = (IdentifiableResourceReloadListener) getIdentifiedSlotLoader()
            .orElseThrow(() -> new IllegalStateException("Unable to get the required SlotLoader listener for TCLayer to add as dependency!"));

        var entitySlotLoader = (IdentifiableResourceReloadListener) getIdentifiedEntitySlotLoader()
            .orElseThrow(() -> new IllegalStateException("Unable to get the required EntitySlotLoader listener for TCLayer to add as dependency!"));

        var callback = new ReloadListenerRegisterCallback(){
            @Override
            public void registerSlotLoader(PreparableReloadListener listener, ResourceLocation id) {
                IdentifiableResourceReloadListener validListener;

                if (listener instanceof IdentifiableResourceReloadListener castedListener) {
                    validListener = castedListener;
                } else {
                    validListener = new ModifiableIdentifiableResourceReloadListener(id, listener);
                }

                manager.registerReloadListener(validListener);

                slotLoader.getFabricDependencies().add(id);
            }

            @Override
            public void registerEntitySlotLoader(PreparableReloadListener listener, ResourceLocation id) {
                IdentifiableResourceReloadListener validListener;

                if (listener instanceof IdentifiableResourceReloadListener castedListener) {
                    validListener = castedListener;
                } else {
                    validListener = new ModifiableIdentifiableResourceReloadListener(id, listener);
                }

                manager.registerReloadListener(validListener);

                entitySlotLoader.getFabricDependencies().add(id);
            }
        };

        for (var value : ModCompatibilityModule.getModules().values()) {
            value.registerDataLoaders(callback);
        }

        DataLoaderBase.LOGGER.info("Registered Trinkets Reloaded Listeners");

        ci.cancel();
    }
}


