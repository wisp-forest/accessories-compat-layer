package io.wispforest.accessories_compat.neoforge;

import io.wispforest.accessories_compat.AccessoriesCompatInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = AccessoriesCompatModNeoforgeInit.MODID)
public class AccessoriesCompatModNeoforgeInit {

    public static final String MODID = "example_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private static IEventBus MOD_BUS = null;

    public AccessoriesCompatModNeoforgeInit(IEventBus modBus, Dist dist) {
        MOD_BUS = modBus;

        LOGGER.info(MODID + " is now loading!");

        AccessoriesCompatInit.init();
    }

    public IEventBus getBus() {
        if (MOD_BUS == null) throw new IllegalStateException("Unable to get the given IEventBus for the following mod: " + MODID);

        return MOD_BUS;
    }
}
