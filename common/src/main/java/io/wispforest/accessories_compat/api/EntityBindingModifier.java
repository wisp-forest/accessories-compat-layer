package io.wispforest.accessories_compat.api;

import com.mojang.logging.LogUtils;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories_compat.api.tags.CollectionAddition;
import net.minecraft.world.entity.EntityType;
import org.slf4j.Logger;

import java.util.*;

public interface EntityBindingModifier {

    Logger LOGGER = LogUtils.getLogger();

    static EntityBindingModifier from(Map<String, SlotType> slotTypes, LinkedHashMap<EntityType<?>, SequencedMap<String, SlotType>> bindingEntries) {
        return type -> {
            var entityBindings = bindingEntries.computeIfAbsent(type, entityType -> new LinkedHashMap<>());

            return new CollectionAddition<>() {
                @Override
                public void add(String accessoryType) {
                    if (!slotTypes.containsKey(accessoryType)) {
                        LOGGER.warn("Unable to locate the given slot for a given entity binding, it will be skipped: [Name: {}]", accessoryType);

                        return;
                    }

                    if (entityBindings.containsKey(accessoryType)) return;

                    entityBindings.put(accessoryType, slotTypes.get(accessoryType));
                }

                @Override
                public void add(Collection<String> accessoryTypes) {
                    accessoryTypes.forEach(this::add);
                }
            };
        };
    }

    CollectionAddition<String> addTo(EntityType<?> type);
}
