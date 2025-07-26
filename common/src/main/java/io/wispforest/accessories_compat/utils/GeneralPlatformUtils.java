package io.wispforest.accessories_compat.utils;

import io.wispforest.accessories_compat.AccessoriesCompatInit;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public interface GeneralPlatformUtils {
    @ApiStatus.Internal
    Logger LOGGER = LoggerFactory.getLogger("GeneralPlatformUtils");

    GeneralPlatformUtils INSTANCE = load(GeneralPlatformUtils.class);

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        LOGGER.debug("Loaded {} for service {}", loadedService, clazz);

        return loadedService;
    }
}
