package io.wispforest.accessories_compat.networking.s2c;

import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Set;

public record ModuleExportedSlots(Map<String, Set<String>> exportedSlots) {
    public static final StructEndec<ModuleExportedSlots> ENDEC = StructEndecBuilder.of(
        Endec.STRING.setOf().mapOf().fieldOf("exported_slots", ModuleExportedSlots::exportedSlots),
        ModuleExportedSlots::new
    );

    public static void handlePacket(ModuleExportedSlots packet, Player player) {
        ModCompatibilityModule.handleSyncPacket(packet);
    }
}
