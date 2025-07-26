package io.wispforest.accessories_compat.neoforge.client;

import io.wispforest.accessories_compat.AccessoriesCompatInit;
import io.wispforest.accessories_compat.client.AccessoriesCompatClientInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = AccessoriesCompatInit.MODID, dist = Dist.CLIENT)
public class ExampleModClientNeoforgeInit {
    public ExampleModClientNeoforgeInit(IEventBus modBus) {
        AccessoriesCompatClientInit.init();
    }
}
