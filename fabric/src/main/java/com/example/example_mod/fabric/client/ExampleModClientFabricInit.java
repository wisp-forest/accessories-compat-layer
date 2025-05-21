package com.example.example_mod.fabric.client;

import com.example.example_mod.client.ExampleModClientInit;
import net.fabricmc.api.ClientModInitializer;

public class ExampleModClientFabricInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExampleModClientInit.init();
    }
}
