package io.wispforest.accessories_compat.api;

import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories_compat.AccessoriesCompatInit;
import io.wispforest.accessories_compat.api.tags.SlotTypesModifier;
import io.wispforest.accessories_compat.networking.s2c.ModuleExportedSlots;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    public interface ResourcePackCallback {

        default void addForced(String modid, ResourceLocation location) {
            add(modid, location, true, true);
        }

        void add(String modid, ResourceLocation location, boolean enabled, boolean forced);
    }

    public void registerDataPacks(ResourcePackCallback callback) {

    }

    public abstract void registerDataLoaders(ReloadListenerRegisterCallback callback);

    public abstract void addEntityBindings(EntityBindingModifier modifier);

    public abstract void addSlotTypes(SlotTypesModifier modifier);

    protected final Set<String> exportedSlotsClient = new HashSet<>();
    protected final Set<String> exportedSlotsServer = new HashSet<>();

    //--

    public static ModuleExportedSlots createSyncPacket() {
        Map<String, Set<String>> map = new HashMap<>();

        MODULES.forEach((id, module) -> {
            map.put(id, module.exportedSlotsServer);
        });

        return new ModuleExportedSlots(map);
    }

    public static void handleSyncPacket(ModuleExportedSlots packet) {
        packet.exportedSlots().forEach((moduleId, strings) -> {
            var module = ModCompatibilityModule.MODULES.get(moduleId);

            if (module == null) return;

            module.exportedSlotsClient.clear();
            module.exportedSlotsClient.addAll(strings);
        });
    }

    public boolean isModSlot(boolean isClient, SlotType type) {
        return (isClient ? exportedSlotsClient : exportedSlotsServer).contains(type.name());
    }

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
