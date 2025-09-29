package io.wispforest.accessories_compat.curios.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import io.wispforest.accessories.menu.SlotTypeAccessible;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import io.wispforest.accessories_compat.utils.SlotRenderingUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.extensions.ICurioSlotExtension;

@Mixin(value = AccessoriesExperimentalScreen.class)
public abstract class AccessoriesExperimentalScreenMixin {

    @WrapOperation(method = "getRenderStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;getItem()Lnet/minecraft/world/item/ItemStack;"))
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
}
