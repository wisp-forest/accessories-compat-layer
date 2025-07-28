package io.wispforest.accessories_compat.curios.wrapper;

import com.google.common.collect.ImmutableMap;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.DropRule;
import io.wispforest.accessories.api.data.AccessoriesBaseData;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories_compat.curios.pond.SlotContextExtension;
import io.wispforest.accessories_compat.utils.ImmutableDelegatingMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.common.capability.ItemizedCurioCapability;
import top.theillusivec4.curios.common.slottype.SlotType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class CuriosConversionUtils {

    public static ICapabilityProvider<ItemStack, Void, ICurio> BASE_PROVIDER = null;

    public static final Map<String, SlotType.Builder> CURRENT_SLOT_BUILDERS = new HashMap<>();
    public static final Map<EntityType<?>, ImmutableMap.Builder<String, ISlotType>> CURRENT_ENTITY_BINDINGS = new HashMap<>();

    public static SlotContext convertToC(SlotReference slotReference) {
        return SlotContextExtension.from(slotReference);
    }

    public static SlotReference convertToA(SlotContext slotContext) {
        var ref = SlotContextExtension.from(slotContext).slotReference();

        // Either use unpacked reference that was used to create slot context from within the cclayer or
        // make one from info on the context
        if (ref == null) {
            ref = SlotReference.of(slotContext.entity(), slotConvertToA(slotContext.identifier()), slotContext.index());
        }

        return ref;
    }

    public static ICurio.DropRule dropRuleConvertToC(DropRule dropRule) {
        return switch (dropRule) {
            case KEEP -> ICurio.DropRule.ALWAYS_KEEP;
            case DROP -> ICurio.DropRule.ALWAYS_DROP;
            case DESTROY -> ICurio.DropRule.DESTROY;
            case DEFAULT -> ICurio.DropRule.DEFAULT;
        };
    }

    public static DropRule dropRuleConvertToA(ICurio.DropRule dropRule) {
        return switch (dropRule) {
            case ALWAYS_KEEP -> DropRule.KEEP;
            case ALWAYS_DROP -> DropRule.DROP;
            case DESTROY -> DropRule.DESTROY;
            case DEFAULT -> DropRule.DEFAULT;
        };
    }

    public static String slotConvertToA(String curiosType) {
        return switch (curiosType) {
            case "curio" -> "any";
            case "head" -> "hat";
            case "body" -> "cape";
            case "bracelet" -> "wrist";
            case "hands" -> "hand";
            case "feet" -> "shoes"; // Special Case for artifacts
            default -> curiosType;
        };
    }

    public static String slotConvertToC(String accessoriesType) {
        return switch (accessoriesType) {
            case "any" -> "curio";
            case "hat" -> "head";
            case "cape" -> "body";
            case "wrist" -> "bracelet";
            case "hand" -> "hands";
            case "shoes" -> "feet"; // Special Case for artifacts
            default -> accessoriesType;
        };
    }

    public static ResourceLocation predicateIdConvertToC(ResourceLocation accessoryPredicateId) {
        if (accessoryPredicateId.equals(AccessoriesBaseData.ALL_PREDICATE_ID)) {
            return ResourceLocation.parse("curios:all");
        } else if (accessoryPredicateId.equals(AccessoriesBaseData.NONE_PREDICATE_ID)) {
            return ResourceLocation.parse("curios:none");
        } else if (accessoryPredicateId.equals(AccessoriesBaseData.TAG_PREDICATE_ID)) {
            return ResourceLocation.parse("curios:tag");
        }

        return accessoryPredicateId;
    }

    public static ResourceLocation convertToA(ResourceLocation curiosPredicateId) {
        return switch (curiosPredicateId.toString()) {
            case "curios:all" -> AccessoriesBaseData.ALL_PREDICATE_ID;
            case "curios:none" -> AccessoriesBaseData.NONE_PREDICATE_ID;
            case "curios:tag" -> AccessoriesBaseData.TAG_PREDICATE_ID;
            default -> curiosPredicateId;
        };
    }

    public static Accessory curioConvertToA(ICurioItem curioItem) {
        return new AccessoryFromCurio(stack -> new ItemizedCurioCapability(curioItem, stack));
    }

    public static Accessory curioConvertToA(ICapabilityProvider<ItemStack, Void, ICurio> icurioProvider) {
        return new AccessoryFromCurio(stack -> icurioProvider.getCapability(stack, null));
    }

    @Nullable
    public static ICurio accessoryConvertToC(Accessory accessory, ItemStack stack) {
        if (accessory instanceof AccessoryFromCurio accessoryFromCurio) {
            return accessoryFromCurio.iCurio(stack).orElse(null);
        }

        return new ItemizedCurioCapability(new CurioFromAccessory(accessory), stack);
    }

    // TODO: GET MORE PERFORMANCE BY WRAPPING MAP WHEN ENTRIES ARE ITERATED!
    //--
//    public static Map<String, ISlotType> convertToC(@Nullable Collection<io.wispforest.accessories.api.slot.SlotType> slots) {
//        if (slots == null) return Map.of();
//
//        return slots
//                .stream()
//                .collect(Collectors.toMap(slotType -> slotConvertSlotToC(slotType.name()), AccessoriesBasedCurioSlot::new));
//    }

    public static Map<String, ISlotType> slotsConvertToC(@Nullable Map<String, io.wispforest.accessories.api.slot.SlotType> slots) {
        if (slots == null) return Map.of();

        return new ImmutableDelegatingMap<>(
            "slot_types", String.class, ISlotType.class,
            slots,
            CuriosConversionUtils::slotConvertToC,
            CuriosConversionUtils::slotConvertToA,
            AccessoriesBasedCurioSlot::new,
            iSlotType -> iSlotType instanceof AccessoriesBasedCurioSlot(
                io.wispforest.accessories.api.slot.SlotType slotType
            ) ? slotType : null
        );
    }

    public static <T> Map<String, T> convertToC(Class<T> clazz, @Nullable Map<String, io.wispforest.accessories.api.slot.SlotType> slots, Function<io.wispforest.accessories.api.slot.SlotType, T> conversionFunc, BiPredicate<io.wispforest.accessories.api.slot.SlotType, T> finderFunc) {
        if (slots == null) return Map.of();

        return new ImmutableDelegatingMap<>(
            "slot_types", String.class, clazz,
            slots,
            CuriosConversionUtils::slotConvertToC,
            CuriosConversionUtils::slotConvertToA,
            conversionFunc,
            t -> {
                for (var value : slots.values()) {
                    if (finderFunc.test(value, t)) return value;
                }

                return null;
            }
        );
    }

    public static net.fabricmc.fabric.api.util.TriState convertToFabric(@NotNull TriState triState) {
        return switch (triState) {
            case FALSE -> net.fabricmc.fabric.api.util.TriState.FALSE;
            case TRUE -> net.fabricmc.fabric.api.util.TriState.TRUE;
            case DEFAULT -> net.fabricmc.fabric.api.util.TriState.DEFAULT;
        };
    }

    public static TriState convertToNeo(@NotNull net.fabricmc.fabric.api.util.TriState triState) {
        return switch (triState) {
            case FALSE -> TriState.FALSE;
            case TRUE -> TriState.TRUE;
            case DEFAULT -> TriState.DEFAULT;
        };
    }
}
