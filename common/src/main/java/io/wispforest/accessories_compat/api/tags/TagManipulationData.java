package io.wispforest.accessories_compat.api.tags;

import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record TagManipulationData(Multimap<ResourceLocation, ResourceLocation> tagMappings, Map<ResourceLocation, LoadedTagData> tagHolders) { }
