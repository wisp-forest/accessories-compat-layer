package io.wispforest.accessories_compat.curios.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.menu.SlotTypeAccessible;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.extensions.ICurioSlotExtension;

@Mixin(BaseOwoHandledScreen.SlotComponent.class)
public abstract class SlotComponentMixin {
    @WrapOperation(method = "drawTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;hasItem()Z"))
    private boolean accessories$hasAnyItem(Slot instance, Operation<Boolean> original) {
        var stack = ItemStack.EMPTY;

        if (instance instanceof SlotTypeAccessible access) {
            var ext = ICurioSlotExtension.from(CuriosConversionUtils.slotConvertToA(access.slotName()));

            if (ext != ICurioSlotExtension.DEFAULT) {
                var ctx = CuriosConversionUtils.objectsConvertToC(access.getContainer().createReference(instance.getContainerSlot()));

                stack = ext.getDisplayStack(ctx, instance.getItem());
            }
        }

        return original.call(instance) || !stack.isEmpty();
    }
}
