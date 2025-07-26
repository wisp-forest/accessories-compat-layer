package io.wispforest.accessories_compat.trinkets.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(value = EnchantmentHelper.class, priority = 1500)
public abstract class EnchantmentHelperMixin {

    @TargetHandler(
        mixin = "dev.emi.trinkets.mixin.EnchantmentHelperMixin",
        name = "forEachTrinket"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private static void preventForEachTrinket(CallbackInfo ci) {
        ci.cancel();
    }

    @TargetHandler(
        mixin = "dev.emi.trinkets.mixin.EnchantmentHelperMixin",
        name = "addTrinketsAsChoices"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private static void preventAddTrinketsAsChoices(CallbackInfo ci) {
        ci.cancel();
    }
}
