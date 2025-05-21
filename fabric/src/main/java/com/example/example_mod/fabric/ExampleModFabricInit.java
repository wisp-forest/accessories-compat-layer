package com.example.example_mod.fabric;

import com.example.example_mod.ExampleModInit;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabricInit implements ModInitializer {
    @Override
    public void onInitialize() {
        ExampleModInit.init();
    }
}
