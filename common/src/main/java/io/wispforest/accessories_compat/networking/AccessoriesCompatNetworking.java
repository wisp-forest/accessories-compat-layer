package io.wispforest.accessories_compat.networking;

import io.wispforest.accessories.networking.AccessoriesNetworking;
import io.wispforest.accessories_compat.networking.s2c.ModuleExportedSlots;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class AccessoriesCompatNetworking {

    public static void init(){
        AccessoriesNetworking.CHANNEL.registerClientboundDeferred(ModuleExportedSlots.class, ModuleExportedSlots.ENDEC);
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        AccessoriesNetworking.CHANNEL.registerClientbound(ModuleExportedSlots.class, ModuleExportedSlots.ENDEC, AccessoriesNetworking.clientHandler(ModuleExportedSlots::handlePacket));
    }
}
