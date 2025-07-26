package io.wispforest.accessories_compat.trinkets.wrapper;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketInventory;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotGroup;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.data.EntitySlotLoader;
import io.wispforest.accessories.data.SlotGroupLoader;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories_compat.trinkets.pond.SlotGroupExtension;
import io.wispforest.accessories_compat.utils.ImmutableDelegatingMap;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class TrinketsWrappingUtils {

    public static final Map<EntityType<?>, Map<String, Set<String>>> slotInfo = new HashMap<>();

    public static final TagKey<Item> ALL_TRINKET_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("tclayer", "all_trinket_items"));

    private static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<SlotReference> createTrinketsReference(io.wispforest.accessories.api.slot.SlotReference slotReference){
        return createTrinketsReference(slotReference, false);
    }

    public static Optional<SlotReference> createTrinketsReference(io.wispforest.accessories.api.slot.SlotReference slotReference, boolean allowAlternativeHand){
        try {
            var capability = AccessoriesCapability.get(slotReference.entity());

            if(capability == null) return Optional.empty();

            var container = capability.getContainers().get(slotReference.slotName());

            var slotType = SlotTypeLoader.getSlotType(slotReference.entity().level(), container.getSlotName());

            var trinketInv = new WrappedTrinketInventory(new WrappedTrinketComponent(slotReference.entity()), container, slotType);

            if (allowAlternativeHand) {
                if (slotReference.slotName().contains("offhand")) {
                    trinketInv.setOtherGroupName("offhand");
                } else if (slotReference.slotName().equals("hand") && (slotReference.slot() % 2 != 0)) {
                    trinketInv.setOtherGroupName("offhand");
                }
            }

            return Optional.of(new SlotReference(trinketInv, slotReference.slot()));
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static Optional<io.wispforest.accessories.api.slot.SlotReference> createAccessoriesReference(SlotReference slotReference){
        if(!(slotReference.inventory() instanceof WrappedTrinketInventory wrappedTrinketInventory)) return Optional.empty();

        return Optional.of(
                io.wispforest.accessories.api.slot.SlotReference.of(
                        wrappedTrinketInventory.container.capability().entity(),
                        wrappedTrinketInventory.container.getSlotName(),
                        slotReference.index()
                )
        );
    }

    public static Map<String, Map<String, SlotType>> getGroupedSlots(boolean isClient, EntityType<?> type) {
        var groups = new HashMap<String, Map<String, SlotType>>();
        var entitySlots = EntitySlotLoader.INSTANCE.getSlotTypes(isClient, type);

        if(entitySlots == null) return Map.of();

        for (var group : SlotGroupLoader.INSTANCE.getGroups(isClient, false)) {
            for (var slot : group.slots()) {
                if(!entitySlots.containsKey(slot)) continue;

                groups.computeIfAbsent(group.name(), string -> new HashMap<>())
                        .put(slot, entitySlots.get(slot));
            }
        }

        return groups;
    }

    public static SlotGroup getGroup(Level level, String accessoriesSlot) {
        var groups = SlotGroupLoader.getGroups(level, false);

        var foundGroup = SlotGroupLoader.getGroup(level, "unsorted").get();

        for (var group : groups) {
            if(group.slots().contains(accessoriesSlot)) {
                foundGroup = group;

                break;
            }
        }

        return foundGroup;
    }

    public static final Set<String> defaultSlots = Set.of("anklet", "back", "belt", "cape", "charm", "face", "hand", "hat", "necklace", "ring", "shoes", "wrist");

    public static SequencedCollection<String> getGroupFromDefaultSlot(String slot) {
        if (!defaultSlots.contains(slot)) return List.of();

        return switch (slot) {
            case "anklet", "shoes" -> List.of("feet");
            case "belt" -> List.of("legs");
            case "hand", "ring", "wrist" -> List.of("hand", "offhand");
            case "face", "hat" -> List.of("head");
            case "back", "cape", "necklace" -> List.of("chest");
            default -> List.of();
        };
    }

    // Unsafe Operation
    public static String trinketsToAccessories_Slot(Optional<String> group, String trinketType){
        return trinketsToAccessories_SlotEither(group, trinketType).map(string -> string, string -> string);
    }

    public static Either<String, String> trinketsToAccessories_SlotEither(Optional<String> group, String trinketType){
        var accessoriesType = switch (trinketType){
            case "glove" -> "hand";
            case "aglet" -> "anklet";
            default -> trinketType;
        };

        if(defaultSlots.contains(accessoriesType)) return Either.right(accessoriesType);

        if(group.isPresent()) accessoriesType = "trinket_group_" + group.get() + "-" + accessoriesType;

        return Either.left(accessoriesType);
    }

    // Safe Operation
    public static String accessoriesToTrinkets_Slot(String accessoryType){
        var trinketType = switch (accessoryType){
            case "hand" -> "glove";
            case "anklet" -> "aglet";
            default -> accessoryType;
        };

        return filterGroupInfo(trinketType);
    }

    public static String trinketsToAccessories_Group(String trinketType){
        return switch (trinketType){
            case "legs" -> "leg";
            case "offhand", "hand" -> "arm";
            case "charm" -> "misc";
            default -> trinketType;
        };
    }

    public static String accessoriesToTrinkets_Group(String accessoryType){
        return switch (accessoryType){
            case "leg" -> "legs";
            case "arm" -> "hand";
            case "misc" -> "charm";
            default -> accessoryType;
        };
    }

    public static ResourceLocation trinketsToAccessories_Validators(ResourceLocation location) {
        return switch (location.toString()){
            case "trinkets:all" -> Accessories.of("all");
            case "trinkets:none" -> Accessories.of("none");
            case "trinkets:tag" -> Accessories.of("tag");
            case "trinkets:relevant" -> Accessories.of("relevant");
            default -> location;
        };
    }

    public static String filterGroupInfo(String trinketType) {
        return trinketType.replaceAll("(trinket_group_).*-", "");
    }

    @Nullable
    public static String getGroupInfo(String trinketType) {
        if (!trinketType.contains("trinket_group_")) return null;

        var groupWithPrefix = trinketType.replace(trinketType.replaceAll("(trinket_group_).*-", ""), "");;

        return groupWithPrefix.replace("trinket_group_", "");
    }

    public static Pair<Optional<String>, String> splitGroupInfo(String path){
        if(!path.contains("/")) return Pair.of(Optional.empty(), path);

        var parts = path.split("/");

        if(parts.length <= 1)  return Pair.of(Optional.empty(), path);

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < parts.length; i++) builder.append(parts[i]);

        return Pair.of(Optional.of(parts[0]), builder.toString());
    }

    public static io.wispforest.accessories.api.DropRule convertDropRule(TrinketEnums.DropRule dropRule){
        return switch (dropRule){
            case KEEP -> io.wispforest.accessories.api.DropRule.KEEP;
            case DROP -> io.wispforest.accessories.api.DropRule.DROP;
            case DESTROY -> io.wispforest.accessories.api.DropRule.DESTROY;
            case DEFAULT -> io.wispforest.accessories.api.DropRule.DEFAULT;
        };
    }

    public static TrinketEnums.DropRule convertDropRule(io.wispforest.accessories.api.DropRule dropRule){
        return switch (dropRule){
            case KEEP -> TrinketEnums.DropRule.KEEP;
            case DROP -> TrinketEnums.DropRule.DROP;
            case DESTROY -> TrinketEnums.DropRule.DESTROY;
            case DEFAULT -> TrinketEnums.DropRule.DEFAULT;
        };
    }

    //--

//    public static <I, V> Map<String, V> of(Class<V> valueClass, Map<String, I> map, UnaryOperator<String> toKeyNamespace, UnaryOperator<String> fromKeyNamespace, Function<I, V> toValueMapFunc, Function<V, @Nullable I> fromValueMapFunc) {
//        return new ImmutableDelegatingMap<>(String.class, valueClass,map, toKeyNamespace, fromKeyNamespace, toValueMapFunc, fromValueMapFunc);
//    }

    public static Map<String, dev.emi.trinkets.api.SlotType> slotType(Map<String, SlotType> map, String group) {
        return new ImmutableDelegatingMap<>(
            "slot_types",
            String.class, dev.emi.trinkets.api.SlotType.class, map,
            TrinketsWrappingUtils::accessoriesToTrinkets_Slot,
            string -> trinketsToAccessories_Slot(Optional.empty(), string),
            slotType -> new WrappedSlotType(slotType, group),
            trinketSlot -> trinketSlot instanceof WrappedSlotType wrappedSlotType ? wrappedSlotType.slotType : null);
    }

    public static Map<String, dev.emi.trinkets.api.SlotGroup> slotGroups(Map<String, Map<String, SlotType>> map, boolean isClientSide) {
        return new ImmutableDelegatingMap<>(
            "slot_group",
            String.class, dev.emi.trinkets.api.SlotGroup.class, map,
            TrinketsWrappingUtils::accessoriesToTrinkets_Group,
            TrinketsWrappingUtils::trinketsToAccessories_Group,
            (group, slotData) -> WrappedSlotGroup.of(group, slotData, isClientSide),
            trinketSlot -> {
                var ext = (SlotGroupExtension) (Object) trinketSlot;

                return ext.isWrapped() ? ext.accessories$slots() : null;
            });
    }

    public static Map<String, Map<String, TrinketInventory>> trinketComponentView(Map<String, Map<String, SlotType>> map, WrappedTrinketComponent component, Map<String, AccessoriesContainer> containerMap, Runnable errorMessage) {
        return (Map<String, Map<String, TrinketInventory>>) (Map) new ImmutableDelegatingMap<>(
            "grouped_trinket_inventories",
            String.class, Map.class, map,
            TrinketsWrappingUtils::accessoriesToTrinkets_Group,
            TrinketsWrappingUtils::trinketsToAccessories_Group,
            (group, slotData) -> groupedTrinketInventories(group, component, slotData, containerMap),
            trinketSlots -> null)
            .errorMessageSupplier(errorMessage);
    }

    public static Map<String, TrinketInventory> groupedTrinketInventories(String group, WrappedTrinketComponent component, Map<String, SlotType> slotTypeMap, Map<String, AccessoriesContainer> containerMap) {
        return new ImmutableDelegatingMap<>(
            "trinket_inventories",
            String.class, TrinketInventory.class, slotTypeMap,
            TrinketsWrappingUtils::accessoriesToTrinkets_Slot,
            (string) -> trinketsToAccessories_Slot(Optional.of(group), string),
            (key, type) -> {
                var container = containerMap.get(type.name());

                if(container == null) throw new IllegalStateException("Unable to get the required Accessories container to wrap for Trinkets API call: [Slot: " + type.name() + "]");

                return new WrappedTrinketInventory(component, container, type);
            },
            object -> null
        );
    }
}
