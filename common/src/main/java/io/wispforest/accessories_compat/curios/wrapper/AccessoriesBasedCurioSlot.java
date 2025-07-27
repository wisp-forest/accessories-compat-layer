package io.wispforest.accessories_compat.curios.wrapper;

import io.wispforest.accessories.api.slot.SlotType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record AccessoriesBasedCurioSlot(SlotType slotType) implements ISlotType {

    @Override public boolean useNativeGui() { return true; }
    @Override public boolean hasCosmetic() { return true; }
    @Override public boolean canToggleRendering() { return true; }

    @Override
    public String getIdentifier() {
        return CuriosConversionUtils.slotConvertSlotToC(this.slotType.name());
    }

    @Override
    public ICurio.DropRule getDropRule() {
        return CuriosConversionUtils.dropRuleConvertToC(this.slotType.dropRule());
    }

    @Override
    public ResourceLocation getIcon() {
        return this.slotType.icon();
    }

    @Override
    public int getOrder() {
        return this.slotType.order();
    }

    @Override
    public int getSize() {
        return this.slotType.amount();
    }

    @Override
    public Set<ResourceLocation> getValidators() {
        return this.slotType.validators().stream()
                .map(CuriosConversionUtils::predicateIdConvertToC)
                .collect(Collectors.toSet());
    }

    @Override
    public int compareTo(@NotNull ISlotType otherType) {
        if (this.getOrder() == otherType.getOrder()) {
            return this.getIdentifier().compareTo(otherType.getIdentifier());
        } else if (this.getOrder() > otherType.getOrder()) {
            return 1;
        }

        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        return equals(this, obj);
    }

    @Override
    public int hashCode() {
        return hashCode(this);
    }

    public static boolean equals(ISlotType slotType, Object o) {
        if (slotType == o) return true;
        if (o == null || slotType.getClass() != o.getClass()) return false;
        top.theillusivec4.curios.common.slottype.SlotType that = (top.theillusivec4.curios.common.slottype.SlotType) o;
        return slotType.getIdentifier().equals(that.getIdentifier());
    }

    public static int hashCode(ISlotType slotType) {
        return Objects.hash(slotType.getIdentifier());
    }
}
