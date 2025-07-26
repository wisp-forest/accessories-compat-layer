package io.wispforest.accessories_compat.api.tags;

import io.wispforest.accessories.data.SlotTypeLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface SlotTypesModifier {

    static SlotTypesModifier of(Map<String, SlotTypeLoader.SlotBuilder> builders) {
        return new SlotTypesModifier() {
            @Override
            public @Nullable SlotTypeLoader.SlotBuilder getBuilder(String accessoryType) {
                return builders.get(accessoryType);
            }

            @Override
            public SlotTypeLoader.SlotBuilder addBuilder(String accessoryType) {
                return builders.computeIfAbsent(accessoryType, SlotTypeLoader.SlotBuilder::new);
            }
        };
    }

    @Nullable
    SlotTypeLoader.SlotBuilder getBuilder(String accessoryType);

    SlotTypeLoader.SlotBuilder addBuilder(String accessoryType);
}
