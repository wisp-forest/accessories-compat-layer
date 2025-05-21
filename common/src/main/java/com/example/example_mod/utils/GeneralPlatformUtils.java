package com.example.example_mod.utils;

import com.example.example_mod.ExampleModInit;

import java.util.ServiceLoader;

public interface GeneralPlatformUtils {
    GeneralPlatformUtils INSTANCE = load(GeneralPlatformUtils.class);

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        ExampleModInit.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
