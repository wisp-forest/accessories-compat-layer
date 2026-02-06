package io.wispforest.accessories_compat.trinkets.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SlotIdRedirect {

    public String trinketsId;
    public String accessoriesId;

    public int additionalSlot;

    public SlotIdRedirect() {
        this("", "", 0);
    }

    public SlotIdRedirect(String trinketsId, String accessoriesId, int additionalSlot) {
        this.trinketsId = trinketsId;
        this.accessoriesId = accessoriesId;
        this.additionalSlot = additionalSlot;
    }

    public static BiMap<String, String> getBiMap(List<SlotIdRedirect> list) {
        var map = HashBiMap.<String, String>create(list.size());
        for (var redirect : list) map.put(redirect.trinketsId, redirect.accessoriesId);
        return map;
    }

    public static Map<String, Pair<String, Integer>> getMap(List<SlotIdRedirect> list) {
        var map = new HashMap<String, Pair<String, Integer>>();
        for (var redirect : list) map.put(redirect.trinketsId, Pair.of(redirect.accessoriesId, redirect.additionalSlot));
        return map;
    }
}
