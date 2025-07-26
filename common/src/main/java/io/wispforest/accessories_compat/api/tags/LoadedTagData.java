package io.wispforest.accessories_compat.api.tags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;

import java.util.List;

public record LoadedTagData(ResourceLocation tagKey, List<TagLoader.EntryWithSource> entries) {
    public LoadedTagData(ResourceLocation tagKey) {
        this(tagKey, List.of());
    }
}
