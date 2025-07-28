package io.wispforest.accessories_compat.curios.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.SlotAttribute;

@Mixin(SlotAttribute.class)
public abstract class SlotAttributeMixin  {
    @WrapOperation(method = "lambda$getOrCreate$0", at = @At(value = "NEW", target = "(Ljava/lang/Object;)Lnet/minecraft/core/Holder$Direct;"))
    private static Holder.Direct createAccessoriesAttribute(Object value, Operation<Holder.Direct> original, @Local(argsOnly = true, ordinal = 0) String id) {
        return (Holder.Direct) io.wispforest.accessories.api.attributes.SlotAttribute.getAttributeHolder(CuriosConversionUtils.slotConvertToA(id));
    }
}
