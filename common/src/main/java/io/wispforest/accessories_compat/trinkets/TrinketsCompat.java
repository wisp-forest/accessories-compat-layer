package io.wispforest.accessories_compat.trinkets;

import com.mojang.datafixers.util.Either;
import dev.emi.trinkets.TrinketsMain;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.TrinketsAttributeModifiersComponent;
import dev.emi.trinkets.data.EntitySlotLoader;
import dev.emi.trinkets.data.SlotLoader;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories_compat.AccessoriesCompatInit;
import io.wispforest.accessories_compat.api.EntityBindingModifier;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.api.ReloadListenerRegisterCallback;
import io.wispforest.accessories_compat.api.tags.SlotTypesModifier;
import io.wispforest.accessories_compat.trinkets.mixin.accessor.SlotLoaderAccessor;
import io.wispforest.accessories_compat.trinkets.utils.SlotIdRedirect;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketsWrappingUtils;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketFromAccessory;
import io.wispforest.accessories_compat.trinkets.wrapper.AccessoryFromTrinket;
import io.wispforest.accessories_compat.utils.LoaderPlatformUtils;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrinketsCompat extends ModCompatibilityModule {

    public static final TrinketsCompat INSTANCE = create();

    protected TrinketsCompat() {
        super("trinkets");
    }

    @Nullable
    private static TrinketsCompat create() {
        if (!LoaderPlatformUtils.INSTANCE.isModLoaded("trinkets")) {
            AccessoriesCompatInit.LOGGER.error("Attempted to load the TrinketsCompat while Trinkets the mod is not loaded!");

            return null;
        }

        return new TrinketsCompat();
    }

    //--

    @Override
    public void registerDataLoaders(ReloadListenerRegisterCallback callback) {
        callback.registerSlotLoader(SlotLoader.INSTANCE, SlotLoader.INSTANCE.getFabricId());
        callback.registerEntitySlotLoader(EntitySlotLoader.SERVER, EntitySlotLoader.SERVER.getFabricId());
    }

    @Override
    public void addEntityBindings(EntityBindingModifier modifier) {
        var redirects = SlotIdRedirect.getMap(AccessoriesCompatInit.CONFIG.slotIdRedirects());

        for (var entry : TrinketsWrappingUtils.CURRENT_SLOT_INFO.entrySet()) {
            var addition = modifier.addTo(entry.getKey());

            var groupedSlots = entry.getValue();

            for (var groupEntry : groupedSlots.entrySet()) {
                var groupName = groupEntry.getKey();

                for (String slotName : groupEntry.getValue()) {
                    var redirect = redirects.get(groupName + "/" + slotName);

                    String accessoryType = redirect != null
                        ? redirect.key()
                        : TrinketsWrappingUtils.trinketsToAccessories_Slot(Optional.of(groupName), slotName);

                    addition.add(accessoryType);
                }
            }
        }

        TrinketsWrappingUtils.CURRENT_SLOT_INFO.clear();
    }

    private final ResourceLocation EMPTY_TEXTURE = ResourceLocation.fromNamespaceAndPath(TrinketsMain.MOD_ID, "gui/slots/empty.png");

    @Override
    public void addSlotTypes(SlotTypesModifier modifier) {
        var redirects = SlotIdRedirect.getMap(AccessoriesCompatInit.CONFIG.slotIdRedirects());

        for (var groupDataEntry : ((SlotLoaderAccessor) SlotLoader.INSTANCE).getLoadedSlots().entrySet()) {
            var groupName = groupDataEntry.getKey();
            var groupData = groupDataEntry.getValue();

            var slots = groupData.getSlots();

            for (var entry : slots.entrySet()) {
                Pair<String, Integer> redirect = redirects.get(groupName + "/" + entry.getKey());

                var accessoryType = redirect != null
                    ? redirect.key()
                    : TrinketsWrappingUtils.trinketsToAccessories_Slot(Optional.of(groupName), entry.getKey());

                var slotData = entry.getValue();

                var builder = modifier.getBuilder(accessoryType);

                if (builder != null) {
                    var slotsCurrentSize = builder.baseAmount;

                    if(slotsCurrentSize != null && slotData.getAmount() > slotsCurrentSize) {
                        builder.amount(slotData.getAmount());
                    }

                    if (redirect != null) {
                        builder.addAmount(redirect.right());
                    }
                } else {
                    builder = modifier.addBuilder(accessoryType);

                    if (slotData.getAmount() != -1) builder.amount(slotData.getAmount());

                    builder.order(slotData.getOrder());

                    var icon = ResourceLocation.parse(slotData.getIcon());

                    if(!icon.equals(EMPTY_TEXTURE)) builder.icon(icon);

                    builder.dropRule(TrinketsWrappingUtils.convertDropRule(TrinketEnums.DropRule.valueOf(slotData.getDropRule())));

                    builder.alternativeTranslation("trinkets.slot." + groupDataEntry.getKey() + "." + entry.getKey());
                }

                for (String validatorPredicate : slotData.getValidatorPredicates()) {
                    var location = ResourceLocation.tryParse(validatorPredicate);

                    if(location == null) continue;

                    builder.validator(TrinketsWrappingUtils.trinketsToAccessories_Validators(location));
                }
            }
        }
    }

    //--

    @Override
    public SequencedSet<ResourceLocation> toAccessoriesTag(ResourceLocation tag) {
        var redirects = SlotIdRedirect.getBiMap(AccessoriesCompatInit.CONFIG.slotIdRedirects());

        var path = tag.getPath();

        var parts = path.split("/");

        if (parts.length != 2) return new LinkedHashSet<>();

        var group = parts[0];
        var slot = parts[1];

        return Either.unwrap(
            TrinketsWrappingUtils.trinketsToAccessories_SlotEither(Optional.of(group), slot)
            .mapRight(slotName -> new LinkedHashSet<>(Set.of(ResourceLocation.fromNamespaceAndPath("accessories", slotName))))
            .mapLeft(slotName -> {
                var entries = new LinkedHashSet<ResourceLocation>();

                var redirect = redirects.get(tag.getPath());

                if (redirect != null) {
                    entries.add(ResourceLocation.fromNamespaceAndPath("accessories", redirect));
                }

                entries.add(ResourceLocation.fromNamespaceAndPath("accessories", slotName));

                return entries;
            })
        );
    }

    @Override
    public SequencedSet<ResourceLocation> fromAccessoriesTag(ResourceLocation tag) {
        var entries = new LinkedHashSet<ResourceLocation>();

        var possibleGroups = TrinketsWrappingUtils.getGroupFromDefaultSlot(tag.getPath());

        for (var group : possibleGroups) {
            entries.add(
                ResourceLocation.fromNamespaceAndPath("trinkets", group + "/" + TrinketsWrappingUtils.accessoriesToTrinkets_Slot(tag.getPath()))
            );
        }

        return entries;
    }

    @Override
    public void getAttributes(ItemStack stack, @Nullable LivingEntity entity, String accessoriesSlotName, int slot, AccessoryAttributeBuilder builder) {
        if (!stack.has(TrinketsAttributeModifiersComponent.TYPE)) return;

        for (var entry : stack.getOrDefault(TrinketsAttributeModifiersComponent.TYPE, TrinketsAttributeModifiersComponent.DEFAULT).modifiers()) {
            if (entry.slot().isEmpty()) {
                builder.addExclusive(entry.attribute(), entry.modifier());
            } else if (entity != null) {
                var group = TrinketsWrappingUtils.getGroup(entity.level(), accessoriesSlotName);

                var slotId = TrinketsWrappingUtils.accessoriesToTrinkets_Group(group.name()) + "/" + TrinketsWrappingUtils.accessoriesToTrinkets_Slot(accessoriesSlotName);

                if (entry.slot().get().equals(slotId)) builder.addExclusive(entry.attribute(), entry.modifier());
            }
        }
    }

    @Override
    public boolean skipOnEquipCheck(ItemStack stack, Accessory accessory) {
        return accessory instanceof AccessoryFromTrinket;
    }

    @Override
    public boolean skipDefaultRenderer(Item item) {
        var trinket = TrinketsApi.getTrinket(item);

        return !(trinket == TrinketsApi.getDefaultTrinket() || trinket instanceof TrinketFromAccessory);
    }
}
