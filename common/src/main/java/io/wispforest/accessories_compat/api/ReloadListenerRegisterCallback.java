package io.wispforest.accessories_compat.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface ReloadListenerRegisterCallback {
    void registerSlotLoader(PreparableReloadListener listener, ResourceLocation id);

    void registerEntitySlotLoader(PreparableReloadListener listener, ResourceLocation id);
}
