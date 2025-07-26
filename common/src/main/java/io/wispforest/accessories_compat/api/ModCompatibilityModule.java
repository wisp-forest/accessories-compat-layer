package io.wispforest.accessories_compat.api;

import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories_compat.AccessoriesCompatInit;
import io.wispforest.accessories_compat.api.tags.SlotTypesModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SequencedSet;

public abstract class ModCompatibilityModule {

    public static final Map<String, ModCompatibilityModule> MODULES = new HashMap<>();

    private final String modid;

    protected ModCompatibilityModule(String modid) {
        this.modid = modid;

        if (MODULES.containsKey(modid)) throw new IllegalStateException("Unable to add CompatibilityModule as such id has been registered already! Id: " + modid);

        MODULES.put(modid, this);

        init();
    }

    public static Map<String, ModCompatibilityModule> getModules() {
        return Collections.unmodifiableMap(MODULES);
    }

    public final String modid() {
        return modid;
    }

    public final ResourceLocation getAllItemTag() {
        return ResourceLocation.fromNamespaceAndPath(AccessoriesCompatInit.MODID, "all_" + modid() + "_items");
    }

    public void init() {}

    //--

    public abstract void registerDataLoaders(ReloadListenerRegisterCallback callback);

    public abstract void addEntityBindings(EntityBindingModifier modifier);

    public abstract void addSlotTypes(SlotTypesModifier modifier);

    //--

    public abstract SequencedSet<ResourceLocation> toAccessoriesTag(ResourceLocation moduleSlotTag);

    public abstract SequencedSet<ResourceLocation> fromAccessoriesTag(ResourceLocation accessoriesSlotTag);

    @Nullable
    public ResourceLocation toAccessoriesTagFirst(ResourceLocation moduleSlotTag) {
        var tags = toAccessoriesTag(moduleSlotTag);

        if (tags.isEmpty()) return null;

        return toAccessoriesTag(moduleSlotTag).getFirst();
    }

    public ResourceLocation fromAccessoriesTagFirst(ResourceLocation accessoriesSlotTag) {
        return fromAccessoriesTag(accessoriesSlotTag).getFirst();
    }

    //--

    public abstract void getAttributes(ItemStack stack, @Nullable LivingEntity entity, String accessoriesSlotName, int slot, AccessoryAttributeBuilder builder);

    public abstract boolean skipOnEquipCheck(ItemStack stack, Accessory accessory);

    public abstract boolean skipDefaultRenderer(Item item);
}
