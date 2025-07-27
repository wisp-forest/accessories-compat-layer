package io.wispforest.accessories_compat.curios;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories_compat.AccessoriesCompatInit;
import io.wispforest.accessories_compat.api.EntityBindingModifier;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.api.ReloadListenerRegisterCallback;
import io.wispforest.accessories_compat.api.tags.SlotTypesModifier;
import io.wispforest.accessories_compat.curios.mixin.accessor.ItemizedCurioCapabilityAccessor;
import io.wispforest.accessories_compat.curios.mixin.accessor.SlotTypeBuilderAccessor;
import io.wispforest.accessories_compat.curios.wrapper.AccessoryFromCurio;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.CuriosConstants;
import top.theillusivec4.curios.api.CurioAttributeModifiers;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.common.data.CuriosEntityManager;
import top.theillusivec4.curios.common.data.CuriosSlotManager;

import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.Set;

public class CuriosCompat extends ModCompatibilityModule {

    public static final CuriosCompat INSTANCE = new CuriosCompat();

    protected CuriosCompat() {
        super("curios");
    }

    @Override
    public void registerDataLoaders(ReloadListenerRegisterCallback callback) {
        callback.registerSlotLoader(CuriosSlotManager.SERVER, ResourceLocation.fromNamespaceAndPath(AccessoriesCompatInit.MODID, "curios_slot_manager"));
        callback.registerSlotLoader(CuriosEntityManager.SERVER, ResourceLocation.fromNamespaceAndPath(AccessoriesCompatInit.MODID, "curios_entity_manager"));
    }

    @Override
    public void addEntityBindings(EntityBindingModifier modifier) {
        for (var entry : CuriosConversionUtils.CURRENT_ENTITY_BINDINGS.entrySet()) {
            var addition = modifier.addTo(entry.getKey());

            for (String curiosId : entry.getValue().build().keySet()) {
                var accessoriesId = CuriosConversionUtils.slotConvertSlotToA(curiosId);

                addition.add(accessoriesId);
            }
        }
    }

    private final ResourceLocation EMPTY_TEXTURE = ResourceLocation.fromNamespaceAndPath(CuriosConstants.MOD_ID, "slot/empty_curio_slot");

    @Override
    public void addSlotTypes(SlotTypesModifier modifier) {
        CuriosConversionUtils.CURRENT_SLOT_BUILDERS.forEach((curiosId, curiosBuilder) -> {
            var accessor = (SlotTypeBuilderAccessor) curiosBuilder;

            var accessoriesId = CuriosConversionUtils.slotConvertSlotToA(curiosId);

            SlotTypeLoader.SlotBuilder builder = modifier.getBuilder(accessoriesId);
            Integer slotsCurrentSize = null;

            if (builder != null) {
                slotsCurrentSize = builder.baseAmount;
            } else {
                builder = modifier.addBuilder(accessoriesId);

                var icon = accessor.getIcon();

                if (icon != null && !icon.equals(EMPTY_TEXTURE)) builder.icon(icon);

                if (accessor.getOrder() != null) builder.order(accessor.getOrder());

                if (accessor.getDropRule() != null) builder.dropRule(CuriosConversionUtils.dropRuleConvertToA(accessor.getDropRule()));

                builder.alternativeTranslation("curios.identifier." + curiosId);
            }

            if (accessor.getSize() != null && slotsCurrentSize != null && accessor.getSize() > slotsCurrentSize) {
                builder.amount(accessor.getSize());
            }

            if (accessor.getSizeMod() != 0) {
                builder.addAmount(accessor.getSizeMod());
            }

            if(accessor.getValidators() != null) {
                for (var validatorPredicate : accessor.getValidators()) {
                    builder.validator(CuriosConversionUtils.convertToA(validatorPredicate));
                }
            }
        });

        CuriosConversionUtils.CURRENT_SLOT_BUILDERS.clear();
    }

    @Override
    public SequencedSet<ResourceLocation> toAccessoriesTag(ResourceLocation moduleSlotTag) {
        return new LinkedHashSet<>(Set.of(ResourceLocation.fromNamespaceAndPath("accessories", CuriosConversionUtils.slotConvertSlotToA(moduleSlotTag.getPath()))));
    }

    @Override
    public SequencedSet<ResourceLocation> fromAccessoriesTag(ResourceLocation accessoriesSlotTag) {
        return new LinkedHashSet<>(Set.of(ResourceLocation.fromNamespaceAndPath("curios", CuriosConversionUtils.slotConvertSlotToC(accessoriesSlotTag.getPath()))));
    }

    @Override
    public void getAttributes(ItemStack stack, @Nullable LivingEntity entity, String accessoriesSlotName, int slot, AccessoryAttributeBuilder builder) {
        Multimap<Holder<Attribute>, AttributeModifier> multimap = LinkedHashMultimap.create();

        if(!stack.has(CuriosRegistry.CURIO_ATTRIBUTE_MODIFIERS.get())) return;

        for (CurioAttributeModifiers.Entry entry : stack.getOrDefault(CuriosRegistry.CURIO_ATTRIBUTE_MODIFIERS.get(), CurioAttributeModifiers.EMPTY).modifiers()) {
            var targetSlot = entry.slot();

            if (targetSlot.equals(CuriosConversionUtils.slotConvertSlotToC(accessoriesSlotName)) || targetSlot.isBlank()) {
                var rl = entry.attribute();

                if (rl == null) continue;

                var attributeModifier = entry.modifier();

                var operation = attributeModifier.operation();
                var amount = attributeModifier.amount();
                var id = attributeModifier.id();

                if (rl.getNamespace().equals("curios")) {
                    var attributeSlotName = rl.getPath();
                    var clientSide = entity == null || entity.level().isClientSide();

                    if (CuriosApi.getSlot(attributeSlotName, clientSide).isPresent()) {
                        CuriosApi.addSlotModifier(multimap, attributeSlotName, id, amount, operation);
                    }
                } else {
                    BuiltInRegistries.ATTRIBUTE.getHolder(rl)
                        .ifPresent(attribute -> multimap.put(attribute, new AttributeModifier(id, amount, operation)));
                }
            }
        }

        multimap.forEach(builder::addExclusive);
    }

    @Override
    public boolean skipOnEquipCheck(ItemStack stack, Accessory accessory) {
        return accessory instanceof AccessoryFromCurio;
    }

    @Override
    public boolean skipDefaultRenderer(Item item) {
        var iCurioItem = CuriosApi.getCurio(item.getDefaultInstance())
            .orElse(null);

        return !(iCurioItem instanceof ItemizedCurioCapabilityAccessor accessor && accessor.getCurioItem() instanceof AccessoryFromCurio);
    }
}
