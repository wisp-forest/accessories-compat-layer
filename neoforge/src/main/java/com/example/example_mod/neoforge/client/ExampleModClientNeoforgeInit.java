package com.example.example_mod.neoforge.client;

import com.example.example_mod.ExampleModInit;
import com.example.example_mod.client.ExampleModClientInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = ExampleModInit.MODID, dist = Dist.CLIENT)
public class ExampleModClientNeoforgeInit {
    public ExampleModClientNeoforgeInit(IEventBus modBus) {
        ExampleModClientInit.init();
    }
}
