package com.example.example_mod.fabric.utils;

import com.example.example_mod.utils.LoaderPlatformUtils;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricLoaderPlatformUtils implements LoaderPlatformUtils {

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }
}
