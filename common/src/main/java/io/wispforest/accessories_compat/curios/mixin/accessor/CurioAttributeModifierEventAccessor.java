package io.wispforest.accessories_compat.curios.mixin.accessor;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@Mixin(CurioAttributeModifierEvent.class)
public interface CurioAttributeModifierEventAccessor {
    @Accessor(remap = false, value = "modifiableModifiers")
    void accessories$setModifiableModifiers(Multimap<Holder<Attribute>, AttributeModifier> multimap);
}
