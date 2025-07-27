package io.wispforest.accessories_compat.curios.wrapper;

import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.DropRule;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.events.extra.AllowWalkingOnSnow;
import io.wispforest.accessories.api.events.extra.EndermanMasked;
import io.wispforest.accessories.api.events.extra.FortuneAdjustment;
import io.wispforest.accessories.api.events.extra.PiglinNeutralInducer;
import io.wispforest.accessories.api.events.extra.v2.LootingAdjustment;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotType;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.CuriosConstants;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils.*;

public class AccessoryFromCurio implements Accessory, LootingAdjustment, FortuneAdjustment, AllowWalkingOnSnow, EndermanMasked, PiglinNeutralInducer {

    private final Function<ItemStack, @Nullable ICurio> getter;

    public AccessoryFromCurio(Function<ItemStack, ICurio> getter) {
        this.getter = getter;
    }

    public Optional<ICurio> iCurio(ItemStack stack) {
        return Optional.ofNullable(this.getter.apply(stack));
    }

    @Override
    public TriState allowWalkingOnSnow(ItemStack itemStack, SlotReference slotReference) {
        return convertBoolean(iCurio(itemStack).map(iCurio -> iCurio.canWalkOnPowderedSnow(convertToC(slotReference))).orElse(false));
    }

    @Override
    public TriState isEndermanMasked(EnderMan enderMan, ItemStack itemStack, SlotReference slotReference) {
        return convertBoolean(iCurio(itemStack).map(iCurio -> iCurio.isEnderMask(convertToC(slotReference), enderMan)).orElse(false));
    }

    @Override
    public TriState makePiglinsNeutral(ItemStack itemStack, SlotReference slotReference) {
        return convertBoolean(iCurio(itemStack).map(iCurio -> iCurio.makesPiglinsNeutral(convertToC(slotReference))).orElse(false));
    }

    @Override
    public int getFortuneAdjustment(ItemStack itemStack, SlotReference slotReference, LootContext lootContext, int i) {
        return iCurio(itemStack).map(iCurio -> iCurio.getFortuneLevel(convertToC(slotReference), lootContext)).orElse(0);
    }

    @Override
    public int getLootingAdjustment(ItemStack itemStack, SlotReference slotReference, LivingEntity livingEntity, LootContext lootContext, DamageSource damageSource, int i) {
        return iCurio(itemStack).map(iCurio -> iCurio.getLootingLevel(convertToC(slotReference), lootContext)).orElse(0);
    }

    private TriState convertBoolean(boolean value) {
        return value ? TriState.TRUE : TriState.DEFAULT;
    }

    //--


    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        iCurio(stack).ifPresent(iCurio -> iCurio.curioTick(convertToC(reference)));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        iCurio(stack).ifPresent(iCurio -> iCurio.onEquip(convertToC(reference), ItemStack.EMPTY));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        iCurio(stack).ifPresent(iCurio -> iCurio.onUnequip(convertToC(reference), ItemStack.EMPTY));
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return iCurio(stack).map(iCurio -> iCurio.canEquip(convertToC(reference))).orElse(false);
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference reference) {
        return iCurio(stack).map(iCurio -> iCurio.canUnequip(convertToC(reference))).orElse(true);
    }

    @Override
    public void getDynamicModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
        var ctx = convertToC(reference);

        Accessory.super.getDynamicModifiers(stack, reference, builder);

        //--

        var id = ResourceLocation.fromNamespaceAndPath(CuriosConstants.MOD_ID, reference.createSlotPath());

        iCurio(stack).ifPresent(iCurio -> iCurio.getAttributeModifiers(ctx, id).forEach(builder::addExclusive));
    }

    @Override
    public DropRule getDropRule(ItemStack stack, SlotReference reference, DamageSource source) {
        return dropRuleConvertToA(iCurio(stack).map(iCurio -> iCurio.getDropRule(convertToC(reference), source, true)).orElse(ICurio.DropRule.DEFAULT));
    }

    @Override
    public void onEquipFromUse(ItemStack stack, SlotReference reference) {
        iCurio(stack).ifPresent(iCurio -> iCurio.onEquipFromUse(convertToC(reference)));
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        try {
            return this.iCurio(stack).map(iCurio -> iCurio.canEquipFromUse(null)).orElse(false);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    @Nullable
    public SoundEventData getEquipSound(ItemStack stack, SlotReference reference) {
        var ctx = convertToC(reference);

        return this.iCurio(stack)
                .map(iCurio -> iCurio.getEquipSound(ctx))
                .map(info -> new SoundEventData(Holder.direct(info.soundEvent()), info.volume(), info.pitch()))
                .orElse(Accessory.super.getEquipSound(stack, reference));
    }

    @Override
    public void onBreak(ItemStack stack, SlotReference reference) {
        iCurio(stack).ifPresent(iCurio -> iCurio.curioBreak(convertToC(reference)));
    }

    @Override
    public void getAttributesTooltip(ItemStack stack, SlotType type, List<Component> tooltips, Item.TooltipContext tooltipContext, TooltipFlag tooltipType) {
        iCurio(stack).ifPresent(iCurio -> {
            var curioList = iCurio.getAttributesTooltip(new ArrayList<>(tooltips), tooltipContext);

            tooltips.clear();
            tooltips.addAll(curioList);
        });
    }

    @Override
    public void getExtraTooltip(ItemStack stack, List<Component> tooltips, Item.TooltipContext tooltipContext, TooltipFlag tooltipType) {
        this.iCurio(stack).ifPresent(iCurio -> tooltips.addAll(iCurio.getSlotsTooltip(new ArrayList<>(), tooltipContext)));
    }
}
