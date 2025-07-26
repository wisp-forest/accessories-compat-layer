package io.wispforest.accessories_compat.trinkets.wrapper;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories_compat.trinkets.pond.CosmeticLookupTogglable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.intellij.lang.annotations.Identifier;

import java.util.Collection;
import java.util.Map;

public class WrappedTrinketInventory extends TrinketInventory {

    public final AccessoriesContainer container;

    public WrappedTrinketInventory(TrinketComponent component, AccessoriesContainer container, SlotType slotType) {
        super(WrappedSlotType.of(slotType, container.capability().entity().level().isClientSide()), component, trinketInventory -> {});

        this.container = container;
    }

    public void setOtherGroupName(String value) {
        ((WrappedSlotType) this.getSlotType()).setOtherGroupName(value);
    }

    @Override
    public Map<ResourceLocation, AttributeModifier> getModifiers() {
        return container.getModifiers();
    }

    @Override
    public Collection<AttributeModifier> getModifiersByOperation(AttributeModifier.Operation operation) {
        return container.getModifiersForOperation(operation);
    }

    @Override
    public void addModifier(AttributeModifier modifier) {
        container.addTransientModifier(modifier);
    }

    @Override
    public void addPersistentModifier(AttributeModifier modifier) {
        container.addPersistentModifier(modifier);
    }

    @Override
    public void removeModifier(ResourceLocation location) {
        container.removeModifier(location);
    }

    @Override
    public void clearModifiers() {
        container.clearModifiers();
    }

    @Override
    public void removeCachedModifier(AttributeModifier attributeModifier) {
        container.getCachedModifiers().remove(attributeModifier);
    }

    @Override
    public void clearCachedModifiers() {
        container.clearCachedModifiers();
    }

    @Override
    public void markUpdate() {
        container.markChanged(false);
    }

    //--

    @Override
    public void clearContent() {
        var accessories = container.getAccessories();
        var cosmetics = container.getCosmeticAccessories();

        for (int i = 0; i < accessories.getContainerSize(); i++) {
            accessories.setItem(i, ItemStack.EMPTY);
            cosmetics.setItem(i, ItemStack.EMPTY);
        }

        this.markUpdate();
    }

    @Override
    public int getContainerSize() {
        return container.getAccessories().getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return container.getAccessories().getContainerSize() != 0;
    }

    @Override
    public ItemStack getItem(int slot) {
        if(this.container.capability().entity() instanceof CosmeticLookupTogglable lookup && lookup.accessories$getLookupToggle()) {
            if(!this.container.shouldRender(slot)) return ItemStack.EMPTY;

            var accessoryStack = container.getCosmeticAccessories().getItem(slot);

            if(accessoryStack.isEmpty()) accessoryStack = container.getAccessories().getItem(slot);

            return accessoryStack;
        }

        return container.getAccessories().getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return container.getAccessories().removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        var stacks = container.getAccessories();

        var itemStack = stacks.getItem(slot);
        stacks.setItem(slot, ItemStack.EMPTY);

        return itemStack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        container.getAccessories().setItem(slot, stack);
    }

    //--
}
