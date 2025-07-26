package io.wispforest.accessories_compat.fabric.client;

import io.wispforest.accessories_compat.client.AccessoriesCompatClientInit;
import net.fabricmc.api.ClientModInitializer;

public class AccessoriesCompatClientFabricInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AccessoriesCompatClientInit.init();
    }
}
