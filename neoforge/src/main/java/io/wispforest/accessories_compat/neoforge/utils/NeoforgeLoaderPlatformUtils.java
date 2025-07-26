package io.wispforest.accessories_compat.neoforge.utils;

import io.wispforest.accessories_compat.utils.LoaderPlatformUtils;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;

public final class NeoforgeLoaderPlatformUtils implements LoaderPlatformUtils {

    @Override
    public Platform getPlatform() {
        return Platform.NEOFORGE;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isModLoaded(String modid) {
        return LoadingModList.get().getModFileById(modid) != null;
    }
}
