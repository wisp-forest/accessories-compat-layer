package io.wispforest.accessories_compat.curios.lang;

import java.util.Map;
import java.util.function.BiFunction;

public class CurioLangRedirects {
    private static final BiFunction<Map<String, String>, String, String> BASE_KEY_CONVERTER = (translations, slot) -> {
        return translations.get("accessories.tooltip.attributes.slot")
                .replace("%s", translations.get("accessories.slot." + slot));
    };

    private static final Map<String, TranslationInjectionEvent.Redirection> CURIOS_TO_ACCESSORIES_TRANSLATIONS = Map.ofEntries(
            Map.entry("curios.identifier.curio", map -> map.get("accessories.slot.any")),
            Map.entry("curios.identifier.necklace", map -> map.get("accessories.slot.necklace")),
            Map.entry("curios.identifier.ring", map -> map.get("accessories.slot.ring")),
            Map.entry("curios.identifier.head", map -> map.get("accessories.slot.hat")),
            Map.entry("curios.identifier.back", map -> map.get("accessories.slot.back")),
            Map.entry("curios.identifier.belt", map -> map.get("accessories.slot.belt")),
            Map.entry("curios.identifier.body", map -> map.get("accessories.slot.cape")),
            Map.entry("curios.identifier.charm", map -> map.get("accessories.slot.charm")),
            Map.entry("curios.identifier.hands", map -> map.get("accessories.slot.hand")),
            Map.entry("curios.identifier.bracelet", map -> map.get("accessories.slot.wrist")),
            Map.entry("curios.modifiers.curio", map -> map.get("accessories.tooltip.attributes.any")),
            Map.entry("curios.modifiers.necklace", map -> BASE_KEY_CONVERTER.apply(map, "necklace")),
            Map.entry("curios.modifiers.ring", map -> BASE_KEY_CONVERTER.apply(map, "ring")),
            Map.entry("curios.modifiers.head", map -> BASE_KEY_CONVERTER.apply(map, "hat")),
            Map.entry("curios.modifiers.back", map -> BASE_KEY_CONVERTER.apply(map, "back")),
            Map.entry("curios.modifiers.belt", map -> BASE_KEY_CONVERTER.apply(map, "belt")),
            Map.entry("curios.modifiers.body", map -> BASE_KEY_CONVERTER.apply(map, "cape")),
            Map.entry("curios.modifiers.charm", map -> BASE_KEY_CONVERTER.apply(map, "charm")),
            Map.entry("curios.modifiers.hands", map -> BASE_KEY_CONVERTER.apply(map, "hand")),
            Map.entry("curios.modifiers.bracelet", map -> BASE_KEY_CONVERTER.apply(map, "wrist"))
    );

    public static void init(){
        TranslationInjectionEvent.AFTER_LANGUAGE_LOAD.register(helper -> helper.addRedirections(CURIOS_TO_ACCESSORIES_TRANSLATIONS));
    }
}
