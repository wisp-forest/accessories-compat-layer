package com.example.example_mod.utils;

import com.example.example_mod.ExampleModInit;

import java.util.ServiceLoader;

/**
 * This class is designed to container only calls to the loader and not API
 * as this can and maybe used in areas like Mixin Plugins.
 */
public interface LoaderPlatformUtils {
    LoaderPlatformUtils INSTANCE = load(LoaderPlatformUtils.class);

    //--

    Platform getPlatform();

    boolean isDevelopmentEnvironment();

    boolean isModLoaded(String modid);

    //--

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        ExampleModInit.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }

    enum Platform {
        FABRIC,
        NEOFORGE
    }
}
