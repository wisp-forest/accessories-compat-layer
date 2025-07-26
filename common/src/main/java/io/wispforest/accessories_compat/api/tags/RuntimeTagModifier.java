package io.wispforest.accessories_compat.api.tags;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RuntimeTagModifier {

    Logger LOGGER = LogUtils.getLogger();

    static RuntimeTagModifier from(Map<ResourceLocation, List<TagLoader.EntryWithSource>> tagEntries) {
        return from(tagEntries, true);
    }

    static RuntimeTagModifier from(Map<ResourceLocation, List<TagLoader.EntryWithSource>> tagEntries, boolean shouldLogAdditions) {
        return toLocation -> {
            var list = tagEntries.computeIfAbsent(toLocation, $ -> new ArrayList<>());

            return fromLocation -> new CollectionAddition<TagLoader.EntryWithSource>() {
                @Override
                public void add(TagLoader.EntryWithSource t) {
                    if (shouldLogAdditions) LOGGER.warn("[RuntimeTagModification] Adding Entry from [{}] to [{}]: \n     {}", fromLocation, toLocation, t);

                    list.add(t);
                }

                @Override
                public void add(Collection<TagLoader.EntryWithSource> collection) {
                    if (collection.isEmpty()) return;

                    if(shouldLogAdditions) LOGGER.warn("[RuntimeTagModification] Adding Entries from [{}] to [{}]: \n     {}", fromLocation, toLocation, collection);

                    list.addAll(collection);
                }
            };
        };
    }

    default TagAddition addTo(LoadedTagData data) {
        return addTo(data.tagKey());
    }

    TagAddition addTo(ResourceLocation location);

    interface TagAddition {
        CollectionAddition from(ResourceLocation location);

        default void from(LoadedTagData data) {
            from(data.tagKey()).add(data.entries());
        }
    }

}
