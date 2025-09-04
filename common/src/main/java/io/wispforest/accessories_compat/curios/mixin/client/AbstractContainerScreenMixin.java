package io.wispforest.accessories_compat.curios.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.menu.SlotTypeAccessible;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.extensions.ICurioSlotExtension;

import java.util.ArrayList;
import java.util.List;

// TODO: REPLACE WITH PROPER API WHEN IMPLEMENTED
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Shadow @Nullable
    protected Slot hoveredSlot;

    @WrapOperation(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack accessories_compat$adjustDisplayStack(Slot instance, Operation<ItemStack> original) {
        var stack = original.call(instance);

        if (instance instanceof SlotTypeAccessible access) {
            var ext = ICurioSlotExtension.from(CuriosConversionUtils.slotConvertToA(access.slotName()));

            if (ext != ICurioSlotExtension.DEFAULT) {
                var ctx = CuriosConversionUtils.objectsConvertToC(access.getContainer().createReference(instance.getContainerSlot()));

                stack = ext.getDisplayStack(ctx, stack);
            }
        }

        return stack;
    }

    @WrapOperation(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;getTooltipFromContainerItem(Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private List<Component> accessories_compat$addThatItsNotReal(AbstractContainerScreen instance, ItemStack stack, Operation<List<Component>> original) {
        boolean displayStack = false;

        if (this.hoveredSlot instanceof SlotTypeAccessible access) {
            var ext = ICurioSlotExtension.from(CuriosConversionUtils.slotConvertToA(access.slotName()));

            if (ext != ICurioSlotExtension.DEFAULT) {
                var ctx = CuriosConversionUtils.objectsConvertToC(access.getContainer().createReference(this.hoveredSlot.getContainerSlot()));

                stack = ext.getDisplayStack(ctx, stack);

                displayStack = true;
            }
        }

        List<Component> tooltips = new ArrayList<>(original.call(instance, stack));

        if(displayStack) {
            tooltips.addLast(Component.literal("[Curios Displayed Stack]"));
        }

        return tooltips;
    }
}
