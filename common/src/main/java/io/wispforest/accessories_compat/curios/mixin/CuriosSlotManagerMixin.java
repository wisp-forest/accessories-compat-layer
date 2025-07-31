package io.wispforest.accessories_compat.curios.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories_compat.curios.wrapper.AccessoriesBasedCurioSlot;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.common.data.CuriosSlotManager;
import top.theillusivec4.curios.common.slottype.SlotType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mixin(CuriosSlotManager.class)
public abstract class CuriosSlotManagerMixin {

    @Shadow public abstract Map<String, ISlotType> getSlots();

    @WrapOperation(
        method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 4)
    )
    private <K, V> Set<Map.Entry<K, V>> saveSlotBuildersForLater(Map<String, SlotType.Builder> instance, Operation<Set<Map.Entry<K, V>>> original) {
        CuriosConversionUtils.CURRENT_SLOT_BUILDERS.clear();
        CuriosConversionUtils.CURRENT_SLOT_BUILDERS.putAll(instance);

        return original.call(Map.of());
    }

    @WrapOperation(
        method = "lambda$apply$1",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/PackResources;listResources(Lnet/minecraft/server/packs/PackType;Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/server/packs/PackResources$ResourceOutput;)V")
    )
    private static void preventCuriosNamespaceFromAffectingThings(PackResources instance, PackType packType, String namespace, String path, PackResources.ResourceOutput resourceOutput, Operation<Void> original) {
        // TODO: SEE IF THIS WILL CAUSE PROBLEMS?
        if (namespace.equals("curios")) return;

        original.call(instance, packType, namespace, path, resourceOutput);
    }

    @Inject(method = "getSlots", at = @At("HEAD"), cancellable = true, remap = false)
    private void getConvertedSlots(CallbackInfoReturnable<Map<String, ISlotType>> cir) {
        cir.setReturnValue(CuriosConversionUtils.slotTypesConvertToC(SlotTypeLoader.INSTANCE.getSlotTypes(isClientSide())));
    }

    @Inject(method = "getSlot", at = @At("HEAD"), cancellable = true, remap = false)
    private void getSlotsFromMethod(String id, CallbackInfoReturnable<Optional<ISlotType>> cir) {
        cir.setReturnValue(
            Optional.ofNullable(
                SlotTypeLoader.INSTANCE.getSlotTypes(this.isClientSide())
                    .get(CuriosConversionUtils.slotConvertToA(id))
            ).map(AccessoriesBasedCurioSlot::new)
        );
    }

    @Inject(method = "getIcons", at = @At("HEAD"), cancellable = true, remap = false)
    private void getConvertedIconsInstead(CallbackInfoReturnable<Map<String, ResourceLocation>> cir) {
        cir.setReturnValue(
            CuriosConversionUtils.objectsConvertToC(ResourceLocation.class,
                SlotTypeLoader.INSTANCE.getSlotTypes(isClientSide()),
                io.wispforest.accessories.api.slot.SlotType::icon,
                (slotType, location) -> slotType.icon().equals(location))
        );
    }

    @Inject(method = "getIcon", at = @At("HEAD"), cancellable = true)
    private void getConvertedIconInstead(String id, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(
            Optional.ofNullable(
                SlotTypeLoader.INSTANCE.getSlotTypes(isClientSide())
                .get(CuriosConversionUtils.slotConvertToA(id))
                .icon()
            ).orElse(ResourceLocation.fromNamespaceAndPath(CuriosApi.MODID, "slot/empty_curio_slot"))
        );
    }

    @Unique
    private boolean isClientSide() {
        return ((Object) this) == CuriosSlotManager.CLIENT;
    }
}
