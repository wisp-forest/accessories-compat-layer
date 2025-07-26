package io.wispforest.accessories_compat.fabric;

import io.wispforest.accessories_compat.AccessoriesCompatInit;
import net.fabricmc.api.ModInitializer;

public class AccessoriesCompatFabricInit implements ModInitializer {
    @Override
    public void onInitialize() {
        AccessoriesCompatInit.init();
    }
}
