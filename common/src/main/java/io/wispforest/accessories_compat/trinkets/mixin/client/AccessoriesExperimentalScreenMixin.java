package io.wispforest.accessories_compat.trinkets.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import io.wispforest.accessories_compat.utils.SlotRenderingUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AccessoriesExperimentalScreen.class)
public abstract class AccessoriesExperimentalScreenMixin {

    @WrapOperation(
            method = "renderSlotTexture",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(IIIIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
    private static void adjustSlotRender(GuiGraphics instance, int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite, Operation<Void> original, @Local(argsOnly = true) Slot slot, @Local(ordinal = 0) Pair<ResourceLocation, ResourceLocation> pair) {
        if (SlotRenderingUtils.renderSlotTexture(instance, x, y, blitOffset, width, height, sprite, slot, pair)) return;

        original.call(instance, x, y, blitOffset, width, height, sprite);
    }
}
