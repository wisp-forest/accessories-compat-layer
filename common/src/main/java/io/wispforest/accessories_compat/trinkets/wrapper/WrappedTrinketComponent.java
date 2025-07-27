package io.wispforest.accessories_compat.trinkets.wrapper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import dev.emi.trinkets.TrinketModifiers;
import dev.emi.trinkets.api.*;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.data.EntitySlotLoader;
import io.wispforest.accessories.impl.AccessoriesHolderImpl;
import io.wispforest.accessories_compat.trinkets.utils.OuterGroupMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public record WrappedTrinketComponent(LivingEntity entity) implements TrinketComponent {

    private static final Logger LOGGER = LogUtils.getLogger();

    public AccessoriesCapability capability() {
        return AccessoriesCapability.get(entity);
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public Map<String, SlotGroup> getGroups() {
        return TrinketsApi.getEntitySlots(entity().level(), entity().getType());
    }

    @Override
    public Map<String, Map<String, TrinketInventory>> getInventory() {
        var entity = this.entity();

        return new OuterGroupMap(TrinketsWrappingUtils.getGroupedSlots(entity.level().isClientSide(), entity.getType()),
                this,
                capability(),
                (additionalMsg) -> {
                    LOGGER.warn("Unable to get some value leading to an error, here comes the dumping data!");
                    LOGGER.warn("Entity: {}", this.entity());
                    LOGGER.warn("Entity Slots: {}", EntitySlotLoader.getEntitySlots(this.entity()));
                    LOGGER.warn("Current Containers: {}", capability().getContainers());
                    LOGGER.warn("More Info: ({})", additionalMsg);
                });
    }

    @Override
    public void update() {
        capability().updateContainers();
    }

    @Override
    public void addTemporaryModifiers(Multimap<String, AttributeModifier> modifiers) {
        capability().addTransientSlotModifiers(modifiers);
    }

    @Override
    public void addPersistentModifiers(Multimap<String, AttributeModifier> modifiers) {
        capability().addPersistentSlotModifiers(modifiers);
    }

    @Override
    public void removeModifiers(Multimap<String, AttributeModifier> modifiers) {
        capability().removeSlotModifiers(modifiers);
    }

    @Override
    public void clearModifiers() {
        capability().clearSlotModifiers();
    }

    @Override
    public Multimap<String, AttributeModifier> getModifiers() {
        return capability().getSlotModifiers();
    }

    @Override
    public boolean isEquipped(Predicate<ItemStack> predicate) {
        return capability().isEquipped(predicate);
    }

    @Override
    public List<Tuple<SlotReference, ItemStack>> getEquipped(Predicate<ItemStack> predicate) {
        var equipped = capability().getEquipped(predicate);

        return equipped.stream()
                .map(slotResult -> {
                    var reference = TrinketsWrappingUtils.createTrinketsReference(slotResult.reference());

                    return reference.map(slotReference -> new Tuple<>(
                            slotReference,
                            slotResult.stack()
                    )).orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<Tuple<SlotReference, ItemStack>> getAllEquipped() {
        return capability().getAllEquipped().stream()
                .map(slotResult -> {
                    var reference = TrinketsWrappingUtils.createTrinketsReference(slotResult.reference());

                    return reference.map(slotReference -> new Tuple<>(
                            slotReference,
                            slotResult.stack()
                    )).orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void forEach(BiConsumer<SlotReference, ItemStack> consumer) {
        for (var tuple : this.getAllEquipped()) {
            consumer.accept(tuple.getA(), tuple.getB());
        }
    }

    @Override
    public Set<TrinketInventory> getTrackingUpdates() {
        return new HashSet<>();
    }

    @Override
    public void clearCachedModifiers() {
        capability().clearCachedSlotModifiers();
    }

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        if (tag.contains("data_written_by_accessories")) return;

        var holder = this.capability().getHolder();

        var dropped = ((AccessoriesHolderImpl) holder).invalidStacks;
        for (String groupKey : tag.getAllKeys()) {
            CompoundTag groupTag = tag.getCompound(groupKey);
            if (groupTag != null) {
                Map<String, TrinketInventory> groupSlots = this.getInventory().get(groupKey);
                if (groupSlots != null) {
                    for (String slotKey : groupTag.getAllKeys()) {
                        CompoundTag slotTag = groupTag.getCompound(slotKey);
                        ListTag list = slotTag.getList("Items", Tag.TAG_COMPOUND);
                        TrinketInventory inv = groupSlots.get(slotKey);

                        if (inv != null) {
                            inv.fromTag(slotTag.getCompound("Metadata"));
                        }

                        for (int i = 0; i < list.size(); i++) {
                            CompoundTag c = list.getCompound(i);
                            ItemStack stack = ItemStack.parseOptional(registryLookup, c);
                            if (inv != null && i < inv.getContainerSize()) {
                                inv.setItem(i, stack);
                            } else {
                                dropped.add(stack);
                            }
                        }
                    }
                } else {
                    for (String slotKey : groupTag.getAllKeys()) {
                        CompoundTag slotTag = groupTag.getCompound(slotKey);
                        ListTag list = slotTag.getList("Items", Tag.TAG_COMPOUND);
                        for (int i = 0; i < list.size(); i++) {
                            CompoundTag c = list.getCompound(i);
                            dropped.add(ItemStack.parseOptional(registryLookup, c));
                        }
                    }
                }
            }
        }

        for (var entryRef : this.capability().getAllEquipped()) {
            var reference = entryRef.reference();
            var slotType = reference.type();

            if (AccessoriesAPI.getPredicateResults(slotType.validators(), reference.entity().level(), reference.entity(), slotType, 0, entryRef.stack()))
                continue;

            dropped.add(entryRef.stack().copy());

            entryRef.reference().setStack(ItemStack.EMPTY);
        }

        // Do not decode attribute modifiers stuff as things maybe incorrect
        if (true) return;

        Multimap<String, AttributeModifier> slotMap = HashMultimap.create();
        this.forEach((ref, stack) -> {
            if (!stack.isEmpty()) {
                Multimap<Holder<Attribute>, AttributeModifier> map = TrinketModifiers.get(stack, ref, this.entity());
                for (Holder<Attribute> entityAttribute : map.keySet()) {
                    if (entityAttribute.isBound() && entityAttribute.value() instanceof SlotAttributes.SlotEntityAttribute slotEntityAttribute) {
                        slotMap.putAll(slotEntityAttribute.slot, map.get(entityAttribute));
                    }
                }
            }
        });
        for (Map.Entry<String, Map<String, TrinketInventory>> groupEntry : this.getInventory().entrySet()) {
            for (Map.Entry<String, TrinketInventory> slotEntry : groupEntry.getValue().entrySet()) {
                String group = groupEntry.getKey();
                String slot = slotEntry.getKey();
                String key = group + "/" + slot;
                Collection<AttributeModifier> modifiers = slotMap.get(key);
                TrinketInventory inventory = slotEntry.getValue();
                for (AttributeModifier modifier : modifiers) {
                    inventory.removeCachedModifier(modifier);
                }
                inventory.clearCachedModifiers();
            }
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        tag.put("data_written_by_accessories", new CompoundTag());

        for (Map.Entry<String, Map<String, TrinketInventory>> group : this.getInventory().entrySet()) {
            CompoundTag groupTag = new CompoundTag();
            for (Map.Entry<String, TrinketInventory> slot : group.getValue().entrySet()) {
                CompoundTag slotTag = new CompoundTag();
                ListTag list = new ListTag();
                TrinketInventory inv = slot.getValue();
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    CompoundTag c = (CompoundTag) inv.getItem(i).saveOptional(registryLookup);
                    list.add(c);
                }
                slotTag.put("Metadata", inv.toTag());
                slotTag.put("Items", list);
                groupTag.put(slot.getKey(), slotTag);
            }
            tag.put(group.getKey(), groupTag);
        }
    }
}
