package io.wispforest.accessories_compat.curios;

import com.google.common.collect.HashMultimap;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.DropRule;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.events.*;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.CuriosConstants;
import top.theillusivec4.curios.api.event.*;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AccessoriesEventHooks {

    public static void initAccessoriesEventHooks() {
        CanUnequipCallback.EVENT.register((stack, reference) -> {
            return CuriosConversionUtils.convertToFabric(
                    NeoForge.EVENT_BUS.post(new CurioCanEquipEvent(stack, CuriosConversionUtils.convertToC(reference), TriState.DEFAULT))
                            .getEquipResult()
            );
        });

        CanEquipCallback.EVENT.register((stack, reference) -> {
            return CuriosConversionUtils.convertToFabric(
                    NeoForge.EVENT_BUS.post(new CurioCanUnequipEvent(stack, CuriosConversionUtils.convertToC(reference)))
                            .getUnequipResult()
            );
        });

        ContainersChangeCallback.EVENT.register((livingEntity, capability, changedContainers) -> {
            if (changedContainers.isEmpty()) return;

            var convertedSlots = changedContainers.keySet().stream()
                    .map(container -> CuriosConversionUtils.slotConvertSlotToC(container.getSlotName()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            NeoForge.EVENT_BUS.post(new SlotModifiersUpdatedEvent(livingEntity, convertedSlots));
        });

        AccessoryChangeCallback.EVENT.register((prevStack, currentStack, reference, stateChange) -> {
            NeoForge.EVENT_BUS.post(new CurioChangeEvent(reference.entity(), reference.slotName(), reference.slot(), prevStack, currentStack));
        });

        AdjustAttributeModifierCallback.EVENT.register((stack, reference, builder) -> {
            var slotLocation = ResourceLocation.fromNamespaceAndPath(CuriosConstants.MOD_ID, AccessoryAttributeBuilder.createSlotPath(reference));

            NeoForge.EVENT_BUS.post(new CurioAttributeModifierEvent(stack, CuriosConversionUtils.convertToC(reference), slotLocation, HashMultimap.create()))
                    .getModifiers()
                    .forEach(builder::addExclusive);
        });

        DeathWrapperEventsImpl.init();
    }

    private static class DeathWrapperEventsImpl implements OnDeathCallback, OnDropCallback {

        public static final DeathWrapperEventsImpl INSTANCE = new DeathWrapperEventsImpl();

        public static void init() {
            OnDeathCallback.EVENT.register(INSTANCE);
            OnDropCallback.EVENT.register(INSTANCE);
        }

        @Nullable
        private DropRulesEvent latestDropRules = null;

        @Override
        public net.fabricmc.fabric.api.util.TriState shouldDrop(net.fabricmc.fabric.api.util.TriState currentState, LivingEntity entity, AccessoriesCapability capability, DamageSource damageSource, List<ItemStack> droppedStacks) {
            var handler = new CurioInventoryCapability(entity);

            var itemEntities = droppedStacks.stream()
                    .map(stack -> {
                        var itemEntity = EntityType.ITEM.create(entity.level());

                        if(itemEntity == null) return null;

                        itemEntity.setItem(stack);

                        return itemEntity;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            droppedStacks.clear();

            var dropEventTest = NeoForge.EVENT_BUS.post(new CurioDropsEvent(entity, handler, damageSource, itemEntities, 0, false));

            droppedStacks.addAll(itemEntities.stream().map(ItemEntity::getItem).toList());

            if(dropEventTest.isCanceled()) return net.fabricmc.fabric.api.util.TriState.FALSE;

            this.latestDropRules = NeoForge.EVENT_BUS.post(new DropRulesEvent(entity, handler, damageSource, 0, false));

            return net.fabricmc.fabric.api.util.TriState.DEFAULT;
        }

        @Override
        public @Nullable DropRule onDrop(DropRule dropRule, ItemStack stack, SlotReference reference, DamageSource damageSource) {
            if(latestDropRules != null) {
                for (var override : latestDropRules.getOverrides()) {
                    if (override.getA().test(stack)) return CuriosConversionUtils.dropRuleConvertToA(override.getB());
                }
            }

            return null;
        }
    }
}
