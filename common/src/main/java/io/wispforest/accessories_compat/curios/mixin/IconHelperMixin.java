package io.wispforest.accessories_compat.curios.mixin;

import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.client.IconHelper;

import java.util.Optional;

@Mixin(IconHelper.class)
public abstract class IconHelperMixin {
    /**
     * @author blodhgarm
     * @reason get icon from accessories slot type
     */
    @Overwrite
    public ResourceLocation getIcon(String identifier) {
        return Optional.ofNullable(SlotTypeLoader.INSTANCE.getSlotTypes(true).get(CuriosConversionUtils.slotConvertToA(identifier)))
            .map(SlotType::icon)
            .orElse(ResourceLocation.fromNamespaceAndPath(CuriosApi.MODID, "slot/empty_curio_slot"));
    }
}
