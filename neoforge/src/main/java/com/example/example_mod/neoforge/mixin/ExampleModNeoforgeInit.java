package com.example.example_mod.neoforge.mixin;

import com.example.example_mod.ExampleModInit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = ExampleModInit.MODID)
public class ExampleModNeoforgeInit {

    private static IEventBus MOD_BUS = null;

    public ExampleModNeoforgeInit(IEventBus modBus, Dist dist) {
        MOD_BUS = modBus;

        ExampleModInit.init();
    }

    public IEventBus getBus() {
        if (MOD_BUS == null) throw new IllegalStateException("Unable to get the given IEventBus for the following mod: " + ExampleModInit.MODID);

        return MOD_BUS;
    }
}
