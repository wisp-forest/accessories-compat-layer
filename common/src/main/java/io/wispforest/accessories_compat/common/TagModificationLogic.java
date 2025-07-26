package io.wispforest.accessories_compat.common;

import com.google.common.collect.HashMultimap;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.api.tags.LoadedTagData;
import io.wispforest.accessories_compat.api.tags.RuntimeTagModifier;
import io.wispforest.accessories_compat.api.tags.TagManipulationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;

import java.util.*;

public class TagModificationLogic {

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static void modify(Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) {
        var modules = ModCompatibilityModule.getModules();

        if (modules.isEmpty()) return;

        var modulesManipulationData = new HashMap<ModCompatibilityModule, TagManipulationData>();

        for (var module : modules.values()) {
            modulesManipulationData.put(module, new TagManipulationData(HashMultimap.create(), new HashMap<>()));
        }

        var accessoryManipulationData = new TagManipulationData(HashMultimap.create(), new HashMap<>());

        map.forEach((location, entries) -> {
            var possibleModule = modules.get(location.getNamespace());

            var entriesCopy = new ArrayList<>(entries);

            if (possibleModule != null) {
                var moduleTag = location;

                var manipulationData = modulesManipulationData.get(possibleModule);

                for (var accessoryTag : possibleModule.toAccessoriesTag(moduleTag)) {
                    accessoryManipulationData.tagMappings().put(moduleTag, accessoryTag);
                    manipulationData.tagMappings().put(accessoryTag, moduleTag);

                    manipulationData.tagHolders().put(moduleTag, new LoadedTagData(moduleTag, entriesCopy));
                }
            } else if(location.getNamespace().equals("accessories")) {
                var accessoryTag = location;

                for (var module : modules.values()) {
                    var manipulationData = modulesManipulationData.get(module);

                    for (var moduleTag : module.fromAccessoriesTag(accessoryTag)) {
                        accessoryManipulationData.tagMappings().put(moduleTag, accessoryTag);
                        manipulationData.tagMappings().put(accessoryTag, moduleTag);
                        
                        accessoryManipulationData.tagHolders().put(accessoryTag, new LoadedTagData(accessoryTag, entriesCopy));
                    }
                }
            }
        });

        var lookup = RuntimeTagModifier.from(map);

        var allAccessoryTags = new LinkedHashSet<ResourceLocation>();
        var moduleToTags = new HashMap<ModCompatibilityModule, List<LoadedTagData>>();

        modulesManipulationData.forEach((module, data) -> {
            allAccessoryTags.addAll(data.tagMappings().keySet());
            allAccessoryTags.addAll(accessoryManipulationData.tagMappings().values());

            moduleToTags.computeIfAbsent(module, $ -> new ArrayList<>())
                .addAll(data.tagHolders().values());
        });

        RuntimeTagModifier.LOGGER.info("All Tags to be dealt with: {}", allAccessoryTags);

        for (var accessoryLocation : allAccessoryTags) {
            List<LoadedTagData> otherModulesTagData = new ArrayList<>();

            var accessoriesTagData = accessoryManipulationData.tagHolders().computeIfAbsent(accessoryLocation, LoadedTagData::new);
            var accessoryTagModification = lookup.addTo(accessoriesTagData);

            for (var maps : modulesManipulationData.values()) {
                var moduleTags = maps.tagHolders();

                for (var moduleLocation : maps.tagMappings().get(accessoryLocation)) {
                    var moduleTagData = moduleTags.computeIfAbsent(moduleLocation, LoadedTagData::new);
                    var moduleTagModification = lookup.addTo(moduleTagData);

                    accessoryTagModification.from(moduleTagData);

                    moduleTagModification.from(accessoriesTagData);

                    for (var otherModuleTagData : otherModulesTagData) {
                        moduleTagModification.from(otherModuleTagData);

                        lookup.addTo(otherModuleTagData)
                            .from(moduleTagData);
                    }

                    otherModulesTagData.add(moduleTagData);
                }
            }
        }

        lookup = RuntimeTagModifier.from(map, false);

        for (var entry : moduleToTags.entrySet()) {
            var tagAddition = lookup.addTo(entry.getKey().getAllItemTag());

            entry.getValue().forEach(tagAddition::from);
        }
    }
}
