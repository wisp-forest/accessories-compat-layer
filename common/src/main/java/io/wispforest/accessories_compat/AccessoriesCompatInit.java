package io.wispforest.accessories_compat;

import com.google.common.reflect.Reflection;
import io.wispforest.accessories_compat.common.AccessoriesCompatConfig;
import io.wispforest.accessories_compat.curios.CuriosCompat;
import io.wispforest.accessories_compat.trinkets.TrinketsCompat;
import io.wispforest.accessories_compat.utils.LoaderPlatformUtils;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessoriesCompatInit {

    public static final String MODID = "accessories_compat_layer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final AccessoriesCompatConfig CONFIG = AccessoriesCompatConfig.createAndLoad();

    public static void init() {
        LOGGER.info(MODID + " is now loading!");

        if (LoaderPlatformUtils.INSTANCE.isModLoaded("trinkets")) {
            Reflection.initialize(TrinketsCompat.class);
        }

        if (LoaderPlatformUtils.INSTANCE.isModLoaded("curios")) {
            Reflection.initialize(CuriosCompat.class);
        }
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
