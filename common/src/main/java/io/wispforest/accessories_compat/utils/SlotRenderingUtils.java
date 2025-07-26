package io.wispforest.accessories_compat.utils;

import com.mojang.datafixers.util.Pair;
import io.wispforest.accessories.api.menu.AccessoriesBasedSlot;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public class SlotRenderingUtils {

    public static boolean renderSlotTexture(GuiGraphics instance, int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite, Slot slot, Pair<ResourceLocation, ResourceLocation> slotTexture) {
        if(slot instanceof AccessoriesBasedSlot && sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
            var location = ResourceLocation.fromNamespaceAndPath(slotTexture.getSecond().getNamespace(), "textures/" + slotTexture.getSecond().getPath() + ".png");

            // Little bug with Accessories stupid batching...
            if (instance instanceof OwoUIDrawContext context && context.recording()) {
                try {
                    context.submitQuads();
                } catch (Exception ignored) {}
            }

            instance.blit(location, x, y, blitOffset, 0, 0, width, height, width, height);

            return true;
        }

        return false;
    }
}
