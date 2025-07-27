package io.wispforest.accessories_compat.neoforge;

import io.wispforest.accessories_compat.AccessoriesCompatInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = AccessoriesCompatInit.MODID)
public class AccessoriesCompatModNeoforgeInit {

    private static IEventBus MOD_BUS = null;

    public AccessoriesCompatModNeoforgeInit(IEventBus modBus, Dist dist) {
        MOD_BUS = modBus;

        AccessoriesCompatInit.init();
    }

    public IEventBus getBus() {
        if (MOD_BUS == null) throw new IllegalStateException("Unable to get the given IEventBus for the following mod: " + AccessoriesCompatInit.MODID);

        return MOD_BUS;
    }
}
