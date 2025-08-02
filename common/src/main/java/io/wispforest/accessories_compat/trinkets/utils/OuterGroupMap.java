package io.wispforest.accessories_compat.trinkets.utils;

import dev.emi.trinkets.api.TrinketInventory;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories_compat.AccessoriesCompatInit;
import io.wispforest.accessories_compat.trinkets.wrapper.EmptyTrinketInventory;
import io.wispforest.accessories_compat.trinkets.wrapper.WrappedTrinketComponent;
import io.wispforest.accessories_compat.trinkets.wrapper.WrappedTrinketInventory;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketsWrappingUtils;
import io.wispforest.accessories_compat.utils.ImmutableWrappingCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OuterGroupMap implements Map<String, Map<String, TrinketInventory>>  {

    private final Map<String, Map<String, io.wispforest.accessories.api.slot.SlotType>> groupedAccessorySlots;

    private final WrappedTrinketComponent trinketComponent;
    private final AccessoriesCapability capability;

    private final Consumer<String> errorMessage;

    public OuterGroupMap(Map<String, Map<String, io.wispforest.accessories.api.slot.SlotType>> groupedAccessorySlots,
                         WrappedTrinketComponent trinketComponent,
                         AccessoriesCapability capability,
                         Consumer<String> errorMessage) {
        this.groupedAccessorySlots = groupedAccessorySlots;

        this.trinketComponent = trinketComponent;
        this.capability = capability;

        this.errorMessage = errorMessage;
    }

    @Override
    public Map<String, TrinketInventory> get(Object key) {
        if (!(key instanceof String str)) return null;

        return new InnerSlotMap(str);
    }

    @Override
    public int size() {
        return this.groupedAccessorySlots.size();
    }

    @Override
    public boolean isEmpty() {
        return this.groupedAccessorySlots.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String str)) return false;

        return this.groupedAccessorySlots.containsKey(TrinketsWrappingUtils.trinketsToAccessories_Group(str));
    }

    @Override
    public boolean containsValue(Object value) {
        return true;
    }

    @Override
    @NotNull
    public Set<String> keySet() {
        return this.groupedAccessorySlots.keySet().stream().map(TrinketsWrappingUtils::accessoriesToTrinkets_Group).collect(Collectors.toSet());
    }

    @Override
    @NotNull
    public Collection<Map<String, TrinketInventory>> values() {
        return this.keySet().stream().<Map<String, TrinketInventory>>map(InnerSlotMap::new).toList();
    }

    @Override
    @NotNull
    public Set<Entry<String, Map<String, TrinketInventory>>> entrySet() {
        return this.keySet().stream()
            .map(string -> Map.entry(string, (Map<String, TrinketInventory>) new InnerSlotMap(string)))
            .collect(Collectors.toSet());
    }

    @Override public @Nullable Map<String, TrinketInventory> put(String key, Map<String, TrinketInventory> value) { return null; }
    @Override public Map<String, TrinketInventory> remove(Object key) { return null; }
    @Override public void putAll(@NotNull Map<? extends String, ? extends Map<String, TrinketInventory>> m) {}
    @Override public void clear() {}

    public class InnerSlotMap implements Map<String, TrinketInventory> {

        private final String currentTrinketsGroup;

        public InnerSlotMap(String currentTrinketsGroup) {
            this.currentTrinketsGroup = currentTrinketsGroup;
        }

        @Nullable
        public Map<String, io.wispforest.accessories.api.slot.SlotType> groupMap() {
            return OuterGroupMap.this.groupedAccessorySlots.get(TrinketsWrappingUtils.trinketsToAccessories_Group(this.currentTrinketsGroup));
        }

        @Override
        public int size() {
            var groupMap = groupMap();

            return groupMap != null ? groupMap.size() : 0;
        }

        @Override
        public boolean isEmpty() {
            var groupMap = groupMap();

            return groupMap == null || groupMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            if (!(key instanceof String trinketKey)) return false;

            var redirects = SlotIdRedirect.getBiMap(AccessoriesCompatInit.CONFIG.slotIdRedirects());

            var redirect = redirects.get(this.currentTrinketsGroup + "/" + trinketKey);

            if (redirect != null && SlotTypeLoader.getSlotType(OuterGroupMap.this.capability.entity(), redirect) != null) {
                return true;
            }

            var groupMap = groupMap();

            var accessoryKey = TrinketsWrappingUtils.trinketsToAccessories_Slot(Optional.of(this.currentTrinketsGroup), trinketKey);

            return groupMap != null && groupMap.containsKey(accessoryKey);
        }

        @Override
        public boolean containsValue(Object value) {
            return true;
        }

        @Override
        public TrinketInventory get(Object key) {
            if (!(key instanceof String trinketKey)) return null;

            var redirects = SlotIdRedirect.getBiMap(AccessoriesCompatInit.CONFIG.slotIdRedirects());

            var redirect = redirects.get(this.currentTrinketsGroup + "/" + trinketKey);

            if (redirect != null) {
                var redirectSlot = SlotTypeLoader.getSlotType(OuterGroupMap.this.capability.entity(), redirect);

                if (redirectSlot != null) return create(redirectSlot);
            }

            var groupMap = groupMap();

            if (groupMap == null) {
                errorMessage.accept("Unable to locate the given group: [" + this.currentTrinketsGroup + "]");

                return null;
            }

            var accessoryKey = TrinketsWrappingUtils.trinketsToAccessories_Slot(Optional.of(this.currentTrinketsGroup), trinketKey);

            var slotType = groupMap.get(accessoryKey);

            if (slotType == null) {
                errorMessage.accept("Unable to locate the given slot type: [Trinket Group: " + this.currentTrinketsGroup + ", Trinket Slot: " + trinketKey + "] : [Accessory Group: " + TrinketsWrappingUtils.trinketsToAccessories_Group(this.currentTrinketsGroup) + ", Accessory Slot: " + accessoryKey +"]");

                return null;
            }

            return create(slotType);
        }

        private TrinketInventory create(SlotType type) {
            var container = OuterGroupMap.this.capability.getContainers().get(type.name());

            if(container == null) {
                errorMessage.accept("Unable to get the required Accessories container to wrap for Trinkets API call: [Slot: " + type.name() + "]");

                return null;
            }

            return new WrappedTrinketInventory(OuterGroupMap.this.trinketComponent, container, type);
        }

        @Override
        @NotNull
        public Set<String> keySet() {
            var groupMap = groupMap();

            if (groupMap == null || groupMap.isEmpty()) return Set.of();

            return new ImmutableWrappingCollection<>(
                groupMap.keySet(),
                TrinketsWrappingUtils::accessoriesToTrinkets_Slot,
                (strings, s) -> strings.contains(TrinketsWrappingUtils.trinketsToAccessories_Slot(Optional.of(this.currentTrinketsGroup), s))
            );
        }

        @Override
        @NotNull
        public Collection<TrinketInventory> values() {
            var groupMap = groupMap();

            if (groupMap == null || groupMap.isEmpty()) return Set.of();

            return new ImmutableWrappingCollection<>(
                groupMap.entrySet(),
                kiEntry -> this.create(kiEntry.getValue()),
                (entries, v) -> groupMap.containsValue(((WrappedTrinketInventory) v).getAccessoreisSlotType())
            );
        }

        @Override
        @NotNull
        public Set<Entry<String, TrinketInventory>> entrySet() {
            var groupMap = groupMap();

            if (groupMap == null || groupMap.isEmpty()) return Set.of();

            return new ImmutableWrappingCollection<>(
                groupMap.entrySet(),
                kiEntry -> Map.entry(TrinketsWrappingUtils.accessoriesToTrinkets_Slot(kiEntry.getKey()), this.create(kiEntry.getValue())),
                (entries, v) -> {
                    return groupMap.containsValue(((WrappedTrinketInventory) v).getAccessoreisSlotType());
                }
            );
        }

        @Override public @Nullable TrinketInventory put(String key, TrinketInventory value) { return null; }
        @Override public TrinketInventory remove(Object key) { return null; }
        @Override public void putAll(@NotNull Map<? extends String, ? extends TrinketInventory> m) {}
        @Override public void clear() {}
    }
}
