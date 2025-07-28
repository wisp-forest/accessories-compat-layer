package io.wispforest.accessories_compat.utils;

import io.wispforest.accessories_compat.AccessoriesCompatInit;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

/**
 * This class is designed to container only calls to the loader and not API
 * as this can and maybe used in areas like Mixin Plugins.
 */
public interface LoaderPlatformUtils {
    @ApiStatus.Internal
    Logger LOGGER = LoggerFactory.getLogger("LoaderPlatformUtils");

    LoaderPlatformUtils INSTANCE = load(LoaderPlatformUtils.class);

    //--

    Platform getPlatform();

    boolean isDevelopmentEnvironment();

    boolean isModLoaded(String modid);

    boolean isClientOnlyEnv();

    //--

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }

    enum Platform {
        FABRIC,
        NEOFORGE
    }
}
