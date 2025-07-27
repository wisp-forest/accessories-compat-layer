package io.wispforest.accessories_compat.trinkets.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.data.EntitySlotLoader;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketsWrappingUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(EntitySlotLoader.class)
public abstract class EntitySlotLoaderMixin {

    @WrapOperation(
        method = "lambda$apply$10",
        at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"),
        remap = false
    )
    private static Iterator<EntityType<?>> passEmptyIterator(Set<EntityType<?>> instance, Operation<Iterator<EntityType<?>>> original, @Local(argsOnly = true, ordinal = 2) Map<String, Set<String>> groups) {
        var slotInfo = TrinketsWrappingUtils.CURRENT_SLOT_INFO;

        for (EntityType<?> type : instance) {
            slotInfo.put(type, ImmutableMap.copyOf(groups));
        }

        return Collections.emptyIterator();
    }

    @Inject(
        method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V", ordinal = 1),
        cancellable = true
    )
    private void preventGroupBuilding(Map<String, Map<String, Set<String>>> loader, ResourceManager manager, ProfilerFiller profiler, CallbackInfo ci, @Local(argsOnly = false, ordinal = 1) Map<EntityType<?>, Map<String, SlotGroup.Builder>> groupBuilders) {
        ci.cancel();
    }

    @Inject(method = {
        "sync*"
    }, at = @At("HEAD"), cancellable = true)
    private static void accessories$preventMethodInvocation(CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * @author blodhgarm
     * @reason better than wrap operation
     */
    @Overwrite
    public Map<String, SlotGroup> getEntitySlots(EntityType<?> entityType) {
        var isClient = this == (Object) EntitySlotLoader.CLIENT;

        return TrinketsWrappingUtils.slotGroups(TrinketsWrappingUtils.getGroupedSlots(isClient, entityType), isClient);
    }

//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    protected void apply(Map<String, Map<String, Set<String>>> loader, ResourceManager manager, ProfilerFiller profiler) {
//        var slotInfo = WrappingTrinketsUtils.slotInfo;
//
//        slotInfo.clear();
//
//        Map<String, SlotLoader.GroupData> slots = SlotLoader.INSTANCE.getSlots();
//        Map<EntityType<?>, Map<String, SlotGroup.Builder>> groupBuilders = new HashMap<>();
//
//        loader.forEach((entityName, groups) -> {
//            Set<EntityType<?>> types = new HashSet<>();
//
//            try {
//                if (entityName.startsWith("#")) {
//                    // TODO rewrite this to work with the new tag system
//                    TrinketsMain.LOGGER.error("[trinkets] Attempted to assign entity entry to tag");
//					/*
//					TagKey<EntityType<?>> tag = TagKey.of(Registry.ENTITY_TYPE_KEY, Identifier.of(entityName.substring(1)));
//					List<? extends EntityType<?>> entityTypes = Registry.ENTITY_TYPE.getEntryList(tag)
//							.orElseThrow(() -> new IllegalArgumentException("Unknown entity tag '" + entityName + "'"))
//							.stream()
//							.map(RegistryEntry::value)
//							.toList();
//
//					types.addAll(entityTypes);*/
//                } else {
//                    types.add(Registries.ENTITY_TYPE.getOrEmpty(Identifier.of(entityName))
//                        .orElseThrow(() -> new IllegalArgumentException("Unknown entity '" + entityName + "'")));
//                }
//            } catch (IllegalArgumentException e) {
//                TrinketsMain.LOGGER.error("[trinkets] Attempted to assign unknown entity entry " + entityName);
//            }
//
//            for (EntityType<?> type : types) {
//                slotInfo.put(type, ImmutableMap.copyOf(groups));
//                // Keep code there but just don't run it
//                if (true) continue;
//
//                Map<String, SlotGroup.Builder> builders = groupBuilders.computeIfAbsent(type, (k) -> new HashMap<>());
//                groups.forEach((groupName, slotNames) -> {
//                    SlotLoader.GroupData group = slots.get(groupName);
//
//                    if (group != null) {
//                        SlotGroup.Builder builder = builders.computeIfAbsent(groupName,
//                            (k) -> new SlotGroup.Builder(groupName, group.getSlotId(), group.getOrder()));
//                        slotNames.forEach(slotName -> {
//                            SlotLoader.SlotData slotData = group.getSlot(slotName);
//
//                            if (slotData != null) {
//                                builder.addSlot(slotName, slotData.create(groupName, slotName));
//                            } else {
//                                TrinketsMain.LOGGER.error("[trinkets] Attempted to assign unknown slot " + slotName);
//                            }
//                        });
//                    } else {
//                        TrinketsMain.LOGGER.error("[trinkets] Attempted to assign slot from unknown group " + groupName);
//                    }
//                });
//            }
//        });
//        this.slots.clear();
//
//        if (true) return;
//        groupBuilders.forEach((entity, groups) -> {
//            Map<String, SlotGroup> entitySlots = this.slots.computeIfAbsent(entity, (k) -> new HashMap<>());
//            groups.forEach((groupName, groupBuilder) -> entitySlots.putIfAbsent(groupName, groupBuilder.build()));
//        });
//    }
}
