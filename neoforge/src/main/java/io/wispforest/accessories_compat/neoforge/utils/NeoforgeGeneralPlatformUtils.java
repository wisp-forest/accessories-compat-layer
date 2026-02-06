package io.wispforest.accessories_compat.neoforge.utils;

import io.wispforest.accessories_compat.AccessoriesCompatInit;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.utils.GeneralPlatformUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.ArrayList;
import java.util.List;

public class NeoforgeGeneralPlatformUtils implements GeneralPlatformUtils {
    @Override
    public void registerCompatPack() {
        var data = new ArrayList<PackData>();

        for (var value : ModCompatibilityModule.getModules().values()) {
            value.registerDataPacks((modid, location, enabled, forced) -> {
                data.add(new PackData(modid, location, enabled, forced));
            });
        }

        if (!data.isEmpty()) {
            var container = ModList.get().getModContainerById(AccessoriesCompatInit.MODID).orElseThrow();

            container.getEventBus().<AddPackFindersEvent>addListener(event -> {
                for (var datum : data) {
                    event.addPackFinders(
                        datum.location.withPath(path -> "resourcepacks/" + path),
                        PackType.SERVER_DATA,
                        Component.literal(datum.location.toLanguageKey()),
                        datum.forced ? PackSource.BUILT_IN : PackSource.FEATURE,
                        datum.enabled,
                        Pack.Position.BOTTOM
                    );
                }
            });
        }
    }

    private record PackData(String modid, ResourceLocation location, boolean enabled, boolean forced) {}
}
