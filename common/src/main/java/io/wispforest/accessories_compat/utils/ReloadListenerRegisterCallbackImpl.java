package io.wispforest.accessories_compat.utils;

import io.wispforest.accessories_compat.api.ReloadListenerRegisterCallback;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.ArrayList;
import java.util.List;

public record ReloadListenerRegisterCallbackImpl(List<PreparableReloadListener> beforeSlotListeners, List<PreparableReloadListener> beforeEntityListeners) implements ReloadListenerRegisterCallback {

    public ReloadListenerRegisterCallbackImpl(){
        this(new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public void registerSlotLoader(PreparableReloadListener listener, ResourceLocation id) {
        beforeSlotListeners.add(listener);
    }

    @Override
    public void registerEntitySlotLoader(PreparableReloadListener listener, ResourceLocation id) {
        beforeEntityListeners.add(listener);
    }
}