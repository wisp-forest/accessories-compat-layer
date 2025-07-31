package io.wispforest.accessories_compat.curios.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.data.EntitySlotLoader;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.common.data.CuriosEntityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(CuriosEntityManager.class)
public abstract class CuriosEntityManagerMixin {

    @Shadow
    private Map<EntityType<?>, Map<String, ISlotType>> entitySlots;
    @Unique
    private boolean resetSlotCache = false;

    @WrapOperation(
        method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 2)
    )
    private <K, V> Set<Map.Entry<K, V>> test(Map<EntityType<?>, ImmutableMap.Builder<String, ISlotType>> instance, Operation<Set<Map.Entry<K, V>>> original) {
        CuriosConversionUtils.CURRENT_ENTITY_BINDINGS.clear();
        CuriosConversionUtils.CURRENT_ENTITY_BINDINGS.putAll(instance);

        return original.call(new HashMap<>());
    }

    @Inject(method = "getEntitySlots", at = @At("HEAD"))
    private void getConvertedEntitySlots(EntityType<?> type, CallbackInfoReturnable<Map<String, ISlotType>> cir) {
        if (!(this.entitySlots instanceof HashMap)) {
            this.entitySlots = new HashMap<>();
        }

        this.entitySlots.computeIfAbsent(type, entityType -> {
            return CuriosConversionUtils.slotTypesConvertToC(EntitySlotLoader.INSTANCE.getSlotTypes(((Object) this) == CuriosEntityManager.CLIENT, type));
        });
    }
}
