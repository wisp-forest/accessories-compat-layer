package com.example.example_mod;

import com.example.example_mod.utils.LoaderPlatformUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleModInit {

    public static final String MODID = "example_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static void init() {
        LOGGER.info(MODID + " is now loading!");
    }

    //--

    /**
     * Whether the given mods debug is enabled, this defaults to {@code true} in a development environment.
     */
    public static final boolean DEBUG;

    static {
        boolean debug = LoaderPlatformUtils.INSTANCE.isDevelopmentEnvironment();
        if (System.getProperty(MODID + ".debug") != null) debug = Boolean.getBoolean(MODID + ".debug");

        DEBUG = debug;
    }

    @ApiStatus.Internal
    public static void debugWarn(Logger logger, String message) {
        if (!DEBUG) return;
        logger.warn(message);
    }

    @ApiStatus.Internal
    public static void debugWarn(Logger logger, String message, Object... params) {
        if (!DEBUG) return;
        logger.warn(message, params);
    }

    public static void ifDebugging(Runnable runnable) {
        if (!DEBUG) return;

        runnable.run();
    }

    //--
}
