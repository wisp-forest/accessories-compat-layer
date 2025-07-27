package io.wispforest.accessories_compat.curios.wrapper;

import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.impl.AccessoriesContainerImpl;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
import io.wispforest.accessories_compat.curios.mixin.accessories.AccessoriesContainerImplAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public record AccessoriesBasedStackHandler(AccessoriesContainerImpl container) implements ICurioStacksHandler {

    public AccessoriesBasedStackHandler(AccessoriesContainer container) {
        this(validate(container));
    }

    private static AccessoriesContainerImpl validate(AccessoriesContainer container) {
        if(!(container instanceof AccessoriesContainerImpl container1)) throw new IllegalStateException("The given instance of AccessoriesContainer was found not to be of AccessoriesContainerImpl");

        return container1;
    }

    @Override
    public IDynamicStackHandler getStacks() {
        return new HandlerImpl(this.container, false);
    }

    @Override
    public IDynamicStackHandler getCosmeticStacks() {
        return new HandlerImpl(this.container, true);
    }

    @Override
    public NonNullList<Boolean> getRenders() {
        return NonNullList.of(Boolean.TRUE, this.container.renderOptions().toArray(Boolean[]::new));
    }

    @Override
    public int getSlots() {
        return this.container.getSize();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean hasCosmetic() {
        return true;
    }

    @Override
    public CompoundTag serializeNBT() {
        var registryAccess = this.container.capability().entity().registryAccess();

        CompoundTag compoundNBT = new CompoundTag();
        compoundNBT.putInt("SavedBaseSize", this.container.getBaseSize());
        compoundNBT.put("Stacks", this.getStacks().serializeNBT(registryAccess));
        compoundNBT.put("Cosmetics", this.getCosmeticStacks().serializeNBT(registryAccess));

        ListTag nbtTagList = new ListTag();

        for (int i = 0; i < this.getRenders().size(); i++) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Slot", i);
            tag.putBoolean("Render", this.getRenders().get(i));
            nbtTagList.add(tag);
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Renders", nbtTagList);
        nbt.putInt("Size", this.getRenders().size());
        compoundNBT.put("Renders", nbt);
        compoundNBT.putBoolean("HasCosmetic", true);
        compoundNBT.putBoolean("Visible", true);
        compoundNBT.putBoolean("RenderToggle", true);
        compoundNBT.putString("DropRule", CuriosConversionUtils.dropRuleConvertToC(this.container.slotType().dropRule()).toString());

        var accessor = ((AccessoriesContainerImplAccessor) this.container);

        if (!accessor.persistentModifiers().isEmpty()) {
            ListTag list = new ListTag();

            for (AttributeModifier attributeModifier : accessor.persistentModifiers()) {
                list.add(attributeModifier.save());
            }
            compoundNBT.put("PersistentModifiers", list);
        }

        if (!accessor.modifiers().isEmpty()) {
            ListTag list = new ListTag();
            accessor.modifiers().forEach((uuid, modifier) -> {
                if (!accessor.persistentModifiers().contains(modifier)) {
                    list.add(modifier.save());
                }
            });
            compoundNBT.put("CachedModifiers", list);
        }
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        var registryAccess = this.container.capability().entity().registryAccess();

        // TODO: HANDLE THIS?
//        if (nbt.contains("SizeShift")) {
//            int sizeShift = nbt.getInt("SizeShift");
//
//            if (sizeShift != 0) {
//                this.addLegacyChange(sizeShift);
//            }
//        }

        if (nbt.contains("PersistentModifiers", 9)) {
            ListTag list = nbt.getList("PersistentModifiers", 10);

            for (int i = 0; i < list.size(); ++i) {
                AttributeModifier attributeModifier = AttributeModifier.load(list.getCompound(i));

                if (attributeModifier != null) {
                    this.addPermanentModifier(attributeModifier);
                }
            }
        }

        var accessor = ((AccessoriesContainerImplAccessor) this.container);

        if (nbt.contains("CachedModifiers", 9)) {
            ListTag list = nbt.getList("CachedModifiers", 10);

            for (int i = 0; i < list.size(); ++i) {
                AttributeModifier attributeModifier = AttributeModifier.load(list.getCompound(i));

                if (attributeModifier != null) {
                    accessor.cachedModifiers().add(attributeModifier);
                    this.addTransientModifier(attributeModifier);
                }
            }
        }

        if (nbt.contains("Renders")) {
            CompoundTag tag = nbt.getCompound("Renders");

            var renderHandler = this.container().renderOptions();

            ListTag tagList = tag.getList("Renders", Tag.TAG_COMPOUND);

            for (int i = 0; i < tagList.size(); i++) {
                CompoundTag tags = tagList.getCompound(i);
                int slot = tags.getInt("Slot");

                if (slot >= 0 && slot < renderHandler.size()) {
                    renderHandler.set(slot, tags.getBoolean("Render"));
                }
            }
        }

        if (nbt.contains("Stacks")) {
            this.getStacks().deserializeNBT(registryAccess, nbt.getCompound("Stacks"));
        }

        if (nbt.contains("Cosmetics")) {
            this.getCosmeticStacks().deserializeNBT(registryAccess, nbt.getCompound("Cosmetics"));
        }

        this.update();
    }

    @Override
    public String getIdentifier() {
        return CuriosConversionUtils.slotConvertSlotToC(this.container.getSlotName());
    }

    @Override
    public Map<ResourceLocation, AttributeModifier> getModifiers() {
        return this.container.getModifiers();
    }

    @Override
    public Set<AttributeModifier> getPermanentModifiers() {
        // TODO: FIGURE OUT IF I SHOULD MAKE A METHOD WITHIN ACCESSORIES OR NOT?
        return Set.of();
    }

    @Override
    public Set<AttributeModifier> getCachedModifiers() {
        return this.container.getCachedModifiers();
    }

    @Override
    public Collection<AttributeModifier> getModifiersByOperation(AttributeModifier.Operation operation) {
        return this.container.getModifiersForOperation(operation);
    }

    @Override
    public void addTransientModifier(AttributeModifier modifier) {
        this.container.addTransientModifier(modifier);
    }

    @Override
    public void addPermanentModifier(AttributeModifier modifier) {
        this.container.addPersistentModifier(modifier);
    }

    @Override
    public void removeModifier(ResourceLocation id) {
        this.container.removeModifier(id);
    }

    @Override
    public void clearModifiers() {
        this.container.clearModifiers();
    }

    @Override
    public void clearCachedModifiers() {
        this.container.clearCachedModifiers();
    }

    @Override
    public void copyModifiers(ICurioStacksHandler other) {
        // NO-OP
    }

    @Override
    public void update() {
        this.container.update();
    }

    //TODO: DOUBTFUL IMPLEMENTING THIS IS NEEDED?
    @Override public CompoundTag getSyncTag() { return null; }
    @Override public void applySyncTag(CompoundTag tag) {}

    //--

    @Override public int getSizeShift() { return 0; }
    @Override public void grow(int amount) {}
    @Override public void shrink(int amount) {}

    //--

    public static class HandlerImpl implements IDynamicStackHandler {
        public final AccessoriesContainer container;
        public final ExpandedSimpleContainer accessories;

        public final boolean isCosmetic;

        public final InvWrapper wrapper;

        public HandlerImpl(AccessoriesContainer container, boolean isCosmetic){
            this.container = container;
            this.accessories = (isCosmetic ? container.getCosmeticAccessories() : container.getAccessories());

            this.isCosmetic = isCosmetic;

            this.wrapper = new InvWrapper(accessories);
        }

        //--

        @Override
        public void setPreviousStackInSlot(int slot, @NotNull ItemStack stack) {
            this.accessories.setPreviousItem(slot, stack);
        }

        @Override
        public ItemStack getPreviousStackInSlot(int slot) {
            return this.accessories.getPreviousItem(slot);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            var dummyHandler = new ItemStackHandler(this.accessories.getItems());

            return dummyHandler.serializeNBT(provider);
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            ListTag tagList = nbt.getList("Items", 10);

            this.accessories.fromTag(tagList, provider);
        }

        //--

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            this.wrapper.setStackInSlot(slot, stack);
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return this.wrapper.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int i, ItemStack arg, boolean bl) {
            return this.wrapper.insertItem(i, arg, bl);
        }

        @Override
        public ItemStack extractItem(int i, int j, boolean bl) {
            return this.wrapper.extractItem(i, j, bl);
        }

        @Override
        public int getSlotLimit(int i) {
            return this.wrapper.getSlotLimit(i);
        }

        @Override
        public boolean isItemValid(int i, ItemStack arg) {
            return this.wrapper.isItemValid(i, arg);
        }

        @Override
        public int getSlots() {
            return this.wrapper.getSlots();
        }

        //--

        @Override public void grow(int amount) {}
        @Override public void shrink(int amount) {}
    }
}
