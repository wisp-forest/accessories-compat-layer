package io.wispforest.accessories_compat.trinkets.mixin;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.trinkets.api.SlotAttributes;
import dev.emi.trinkets.api.SlotReference;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketsWrappingUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.intellij.lang.annotations.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SlotAttributes.class)
public abstract class SlotAttributesMixin {

    @WrapMethod(method = "addSlotModifier")
    private static void adjustAddSlotModifier(Multimap<Holder<Attribute>, AttributeModifier> map, String slot, ResourceLocation identifier, double amount, AttributeModifier.Operation operation, Operation<Void> original) {
        var data = TrinketsWrappingUtils.splitGroupInfo(slot);
        var slotType = TrinketsWrappingUtils.trinketsToAccessories_Slot(data.left(), data.right());

        io.wispforest.accessories.api.attributes.SlotAttribute.addSlotModifier(map, slotType, identifier, amount, operation);
    }

    @WrapOperation(method = "getIdentifier", at = @At(value = "INVOKE", target = "Ldev/emi/trinkets/api/SlotReference;getId()Ljava/lang/String;", remap = false))
    private static String replaceAnyInvalidCharacters(SlotReference instance, Operation<String> original) {
        return original.call(instance).replace(":", "-");
    }
}
