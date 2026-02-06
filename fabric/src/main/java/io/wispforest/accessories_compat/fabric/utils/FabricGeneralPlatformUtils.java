package io.wispforest.accessories_compat.fabric.utils;

import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.utils.GeneralPlatformUtils;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricGeneralPlatformUtils implements GeneralPlatformUtils {

    public void registerCompatPack() {
        for (var value : ModCompatibilityModule.getModules().values()) {
            value.registerDataPacks((modid, location, enabled, forced) -> {
                ResourceManagerHelper.registerBuiltinResourcePack(
                    location,
                    FabricLoader.getInstance().getModContainer(modid).orElseThrow(),
                    forced ? ResourcePackActivationType.ALWAYS_ENABLED : (enabled ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL)
                );
            });
        }
    }
}
