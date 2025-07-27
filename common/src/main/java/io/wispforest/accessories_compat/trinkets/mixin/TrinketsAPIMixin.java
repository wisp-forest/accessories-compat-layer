package io.wispforest.accessories_compat.trinkets.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Function3;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories_compat.trinkets.wrapper.*;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mixin(value = TrinketsApi.class, remap = false)
public abstract class TrinketsAPIMixin {

    @Shadow(remap = false) @Final
    private static Map<Item, Trinket> TRINKETS;

    @Shadow(remap = false) @Final @Mutable
    private static Trinket DEFAULT_TRINKET;

    // Register trinkets into accessories
    @Inject(method = "registerTrinket", at = @At("TAIL"), remap = false)
    private static void registerTrinketAsAccessory(Item item, Trinket trinket, CallbackInfo ci) {
        AccessoriesAPI.registerAccessory(item, new AccessoryFromTrinket(trinket));
    }

    // Get accessories if trinket does not exist
    @Inject(method = "getTrinket", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getAccessoryAsTrinket(Item item, CallbackInfoReturnable<Trinket> cir) {
        if (!TRINKETS.containsKey(item)) {
            var accessory = AccessoriesAPI.getAccessory(item);

            if (accessory != null) {
                cir.setReturnValue(new TrinketFromAccessory(accessory));
            }
        }
    }

    /**
     * @author blodhgarm
     * @reason Get accessories Capability instead of the component from the entity
     */
    @Overwrite(remap = false)
    public static Optional<TrinketComponent> getTrinketComponent(LivingEntity livingEntity) {
        if(livingEntity == null) return Optional.empty();

        var capability = AccessoriesCapability.get(livingEntity);

        return Optional.of(capability != null ? new WrappedTrinketComponent(livingEntity) : new EmptyTrinketComponent(livingEntity));
    }

    /**
     * @author blodhgarm
     * @reason Use Accessories API instead of trinkets for break handling
     */
    @Overwrite(remap = false)
    public static void onTrinketBroken(ItemStack stack, SlotReference ref, LivingEntity entity) {
        var slotName = ((WrappedTrinketInventory) ref.inventory()).container.getSlotName();

        var reference = io.wispforest.accessories.api.slot.SlotReference.of(entity, slotName, ref.index());

        AccessoriesAPI.breakStack(reference);
    }

    // Register predicates within accessories as an error catching version
    @Inject(method = "registerTrinketPredicate", at = @At("HEAD"), remap = false)
    private static void registerTrinketPredicateWithinAccessories(ResourceLocation id, Function3<ItemStack, SlotReference, LivingEntity, TriState> predicate, CallbackInfo ci, @Local(argsOnly = true) LocalRef<Function3<ItemStack, SlotReference, LivingEntity, TriState>> predicateRef) {
        if (id.equals(ResourceLocation.fromNamespaceAndPath("trinkets", "relevant"))) {
            predicateRef.set((stack, ref, entity) -> {
                var reference = io.wispforest.accessories.api.slot.SlotReference.of(entity, ref.inventory().getSlotType().getName(), ref.index());
                var builder = new AccessoryAttributeBuilder(reference);

                var accessory = AccessoriesAPI.getOrDefaultAccessory(stack);

                if(accessory != null) accessory.getDynamicModifiers(stack, reference, builder);

                if (!builder.getAttributeModifiers(false).isEmpty()) return TriState.TRUE;
                return TriState.DEFAULT;
            });
        }

        AccessoriesAPI.registerPredicate(id, new SafeSlotBasedPredicate(id, predicateRef.get()));
    }

    /**
     * @author blodhgarm
     * @reason Use Accessories API instead of trinkets when evaluating predicates and just convert trinkets to accessories if possilbe
     */
    @Overwrite(remap = false)
    public static boolean evaluatePredicateSet(Set<ResourceLocation> set, ItemStack stack, SlotReference ref, LivingEntity entity) {
        var convertedSet = new HashSet<ResourceLocation>();

        for (var location : set) {
            var converetdLocation = switch (location.toString()){
                case "trinkets:all" -> Accessories.of("all");
                case "trinkets:none" -> Accessories.of("none");
                case "trinkets:tag" -> Accessories.of("tag");
                case "trinkets:relevant" -> Accessories.of("relevant");
                default -> location;
            };

            convertedSet.add(converetdLocation);
        }

        var slotName = ((WrappedTrinketInventory) ref.inventory()).container.getSlotName();

        var slotType = SlotTypeLoader.getSlotType(entity.level(), slotName);

        if(slotType == null) {
            throw new IllegalStateException("Unable to get a SlotType using the WrappedTrinketInventory from the SlotTypeLoader! [Name: " + slotName +"]");
        }

        return AccessoriesAPI.getPredicateResults(convertedSet, entity.level(), entity, slotType, ref.index(), stack);
    }

    // Set default trinket to accessories default
    @Inject(method = "<clinit>", at = @At(value = "TAIL"), remap = false)
    private static void adjustDefaultTrinket(CallbackInfo ci) {
        DEFAULT_TRINKET = new TrinketFromAccessory(AccessoriesAPI.defaultAccessory());
    }
}
