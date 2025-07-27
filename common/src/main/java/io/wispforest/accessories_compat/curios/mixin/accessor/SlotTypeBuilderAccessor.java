package io.wispforest.accessories_compat.curios.mixin.accessor;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.theillusivec4.curios.CuriosConstants;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.common.data.CuriosSlotManager;
import top.theillusivec4.curios.common.slottype.SlotType;

import java.util.Set;

@Mixin(SlotType.Builder.class)
public interface SlotTypeBuilderAccessor {

     @Accessor(value = "identifier", remap = false)
     String getIdentifier();

     @Accessor(value = "order", remap = false)
     Integer getOrder();

     @Accessor(value = "size", remap = false)
     Integer getSize();

     @Accessor(value = "sizeMod", remap = false)
     int getSizeMod();

     @Accessor(value = "useNativeGui", remap = false)
     Boolean getUseNativeGui();

     @Accessor(value = "hasCosmetic", remap = false)
     Boolean getHasCosmetic();

     @Accessor(value = "renderToggle", remap = false)
     Boolean getRenderToggle();

     @Accessor("icon")
     ResourceLocation getIcon();

     @Accessor(value = "dropRule", remap = false)
     ICurio.DropRule getDropRule();

     @Accessor(value = "validators", remap = false)
     Set<ResourceLocation> getValidators();
}
