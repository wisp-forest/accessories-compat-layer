package io.wispforest.accessories_compat.curios.wrapper;

import com.google.common.collect.Multimap;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.events.extra.AllowWalkingOnSnow;
import io.wispforest.accessories.api.events.extra.EndermanMasked;
import io.wispforest.accessories.api.events.extra.FortuneAdjustment;
import io.wispforest.accessories.api.events.extra.PiglinNeutralInducer;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

import static io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils.*;

public class CurioFromAccessory implements ICurioItem {

    private final Accessory accessory;

    public CurioFromAccessory(Accessory accessory) {
        this.accessory = accessory;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        accessory.tick(stack, convertToA(slotContext));
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        accessory.onEquip(stack, convertToA(slotContext));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        accessory.onUnequip(stack, convertToA(slotContext));
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return accessory.canEquip(stack, convertToA(slotContext));
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return accessory.canUnequip(stack, convertToA(slotContext));
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        var ref = convertToA(slotContext);
        var builder = new AccessoryAttributeBuilder(ref);

        accessory.getDynamicModifiers(stack, ref, builder);

        return builder.getAttributeModifiers(false);
    }

    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        accessory.onEquipFromUse(stack, convertToA(slotContext));
    }

    @Override
    public @NotNull ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        var data = accessory.getEquipSound(stack, convertToA(slotContext));

        return (data == null)
                ? ICurioItem.super.getEquipSound(slotContext, stack)
                : new ICurio.SoundInfo(data.event().value(), data.volume(), data.pitch());
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return accessory.canEquipFromUse(stack);
    }

    @Override
    public void curioBreak(SlotContext slotContext, ItemStack stack) {
        accessory.onBreak(stack, convertToA(slotContext));
    }

    @Override
    public @NotNull ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        return dropRuleConvertToC(accessory.getDropRule(stack, convertToA(slotContext), source));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, Item.TooltipContext context, ItemStack stack) {
        // This is a hack as curios dose not given any flag value or type to say the least
        try {
            accessory.getAttributesTooltip(stack, null, tooltips, context, TooltipFlag.NORMAL);
        } catch (NullPointerException e) {}

        return tooltips;
    }

    //--

    @Override
    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
        if (accessory instanceof FortuneAdjustment adjustment) {
            return adjustment.getFortuneAdjustment(stack, convertToA(slotContext), lootContext, 0);
        }

        return ICurioItem.super.getFortuneLevel(slotContext, lootContext, stack);
    }

    @Override
    public int getLootingLevel(SlotContext slotContext, @Nullable LootContext lootContext, ItemStack stack) {
        if (lootContext != null && lootContext.getParam(LootContextParams.ATTACKING_ENTITY) instanceof LivingEntity target) {
            var damageSource = lootContext.getParamOrNull(LootContextParams.DAMAGE_SOURCE);

            if(damageSource != null) {
                if(accessory instanceof io.wispforest.accessories.api.events.extra.LootingAdjustment lootingAdjustment){
                    return lootingAdjustment.getLootingAdjustment(stack, convertToA(slotContext), target, damageSource, 0);
                } else if(accessory instanceof io.wispforest.accessories.api.events.extra.v2.LootingAdjustment lootingAdjustment){
                    return lootingAdjustment.getLootingAdjustment(stack, convertToA(slotContext), target, lootContext, damageSource, 0);
                }
            }
        }

        return ICurioItem.super.getLootingLevel(slotContext, lootContext, stack);
    }

    @Override
    public boolean makesPiglinsNeutral(SlotContext slotContext, ItemStack stack) {
        var state = TriState.DEFAULT;

        if (accessory instanceof PiglinNeutralInducer inducer) {
            state = inducer.makePiglinsNeutral(stack, convertToA(slotContext));
        }

        return state.orElse(ICurioItem.super.makesPiglinsNeutral(slotContext, stack));
    }

    @Override
    public boolean canWalkOnPowderedSnow(SlotContext slotContext, ItemStack stack) {
        var state = TriState.DEFAULT;

        if (accessory instanceof AllowWalkingOnSnow predicate) {
            state = predicate.allowWalkingOnSnow(stack, convertToA(slotContext));
        }

        return state.orElse(ICurioItem.super.canWalkOnPowderedSnow(slotContext, stack));
    }

    @Override
    public boolean isEnderMask(SlotContext slotContext, EnderMan enderMan, ItemStack stack) {
        var state = TriState.DEFAULT;

        if (accessory instanceof EndermanMasked predicate) {
            state = predicate.isEndermanMasked(enderMan, stack, convertToA(slotContext));
        }

        return state.orElse(ICurioItem.super.isEnderMask(slotContext, enderMan, stack));
    }
}
