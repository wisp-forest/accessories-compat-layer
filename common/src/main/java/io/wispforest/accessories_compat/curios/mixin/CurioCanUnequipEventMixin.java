package io.wispforest.accessories_compat.curios.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.event.CurioCanUnequipEvent;

@Mixin(CurioCanUnequipEvent.class)
public abstract class CurioCanUnequipEventMixin {
    @Shadow private TriState result;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void setDefaultResult(ItemStack stack, SlotContext slotContext, CallbackInfo ci) {
        this.result = TriState.DEFAULT;
    }
}
