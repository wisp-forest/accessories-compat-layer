package io.wispforest.accessories_compat.curios.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories.impl.AccessoriesCapabilityImpl;
import io.wispforest.accessories.impl.AccessoriesHolderImpl;
import io.wispforest.accessories_compat.curios.pond.CurioInventoryCapabilityExtension;
import io.wispforest.accessories_compat.curios.pond.CurioInventoryExtension;
import io.wispforest.accessories_compat.curios.wrapper.AccessoriesBasedStackHandler;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import io.wispforest.accessories_compat.utils.ImmutableDelegatingMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.capability.CurioInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(CurioInventory.class)
public abstract class CurioInventoryMixin implements CurioInventoryExtension {
    @Shadow
    boolean markDeserialized;
    @Shadow
    CompoundTag deserialized;
    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    @Nullable
    @Unique
    private AccessoriesCapabilityImpl capability = null;

    @Nullable
    @Unique
    private AccessoriesHolderImpl holder = null;

    public List<ItemStack> getInvalidStacks() {
        return capability != null ? holder().invalidStacks : List.of();
    }

    @Nullable
    private AccessoriesHolderImpl holder() {
        if (capability != null) {
            if (holder == null) holder = ((AccessoriesHolderImpl) capability.getHolder());

            return holder;
        }

        return null;
    }

    @Inject(method = "init", at = @At("HEAD"), remap = false, cancellable = true)
    private void runInitWithAccessoriesInMind(ICuriosItemHandler curiosItemHandler, CallbackInfo ci) {
        ci.cancel();

        if (!(curiosItemHandler instanceof CurioInventoryCapabilityExtension ext)) {
            LOGGER.error("Unable to init a given CurioInventory due to given ICuriosItemHandler not being instance of CurioInventoryCapability!");

            return;
        }

        capability = ext.capability();
        holder = null;

        if (!this.markDeserialized) {
            // TODO: MAY BE A ISSUES ENCOUNTERED WITHIN THE PAST DUE TO ACCESSORIES BEING INIT FIRST LEADING TO DISAPPEARANCE OF STUFF
            //capability.reset(false);
        } else {
            this.markDeserialized = false;

            if (this.deserialized.getBoolean("AccessoriesEncoded") || this.deserialized.isEmpty()) return;

            readData(capability.entity(), capability, holder(), this.deserialized.getList("Curios", Tag.TAG_COMPOUND));

            this.deserialized = new CompoundTag();
        }
    }

    @Unique
    private static void readData(LivingEntity livingEntity, AccessoriesCapability capability, AccessoriesHolderImpl holder, ListTag data) {
        for (int i = 0; i < data.size(); i++) {
            var tag = data.getCompound(i);
            var curiosId = tag.getString("Identifier");

            var slotType = SlotTypeLoader.getSlotType(livingEntity.level(), CuriosConversionUtils.slotConvertToA(curiosId));

            var container = (slotType != null) ? capability.getContainer(slotType) : null;

            holder.invalidStacks.addAll(deserializeNBT_StackHandler(livingEntity, container, tag.getCompound("StacksHandler")));
        }

        var invalidStacks = holder.invalidStacks;

        for (var entryRef : capability.getAllEquipped()) {
            var reference = entryRef.reference();
            var slotType = reference.type();

            if (AccessoriesAPI.getPredicateResults(slotType.validators(), reference.entity().level(), livingEntity, slotType, 0, entryRef.stack())) continue;

            invalidStacks.add(entryRef.stack().copy());

            entryRef.reference().setStack(ItemStack.EMPTY);
        }
    }

    @Unique
    private static List<ItemStack> deserializeNBT_StackHandler(LivingEntity livingEntity, @Nullable AccessoriesContainer container, CompoundTag nbt){
        var dropped = new ArrayList<ItemStack>();

        if (nbt.contains("Stacks")) {
            dropped.addAll(deserializeNBT_Stacks(livingEntity, container, AccessoriesContainer::getAccessories, nbt.getCompound("Stacks")));
        }

        if (nbt.contains("Cosmetics")) {
            dropped.addAll(deserializeNBT_Stacks(livingEntity, container, AccessoriesContainer::getCosmeticAccessories, nbt.getCompound("Cosmetics")));
        }

        return dropped;
    }

    @Unique
    private static List<ItemStack> deserializeNBT_Stacks(LivingEntity livingEntity, @Nullable AccessoriesContainer container, Function<AccessoriesContainer, Container> containerFunc, CompoundTag nbt){
        var list = nbt.getList("Items", Tag.TAG_COMPOUND)
            .stream()
            .map(tagEntry -> ItemStack.parseOptional(livingEntity.registryAccess(), (tagEntry instanceof CompoundTag compoundTag) ? compoundTag : new CompoundTag()))
            .toList();

        var dropped = new ArrayList<ItemStack>();

        if(container != null) {
            var accessories = containerFunc.apply(container);

            for (var stack : list) {
                boolean consumedStack = false;

                for (int i = 0; i < accessories.getContainerSize() && !consumedStack; i++) {
                    var currentStack = accessories.getItem(i);

                    if (!currentStack.isEmpty()) continue;

                    accessories.setItem(i, stack.copy());

                    consumedStack = true;
                }

                if (!consumedStack) dropped.add(stack.copy());
            }
        } else {
            dropped.addAll(list);
        }

        return dropped;
    }

    @Inject(method = "asMap", at = @At("HEAD"), cancellable = true, remap = false)
    private void getMapFromAccessoriesHolder(CallbackInfoReturnable<Map<String, ICurioStacksHandler>> cir) {
        if (capability == null) {
            cir.setReturnValue(Map.of());
        } else {
            var holder = holder();

            cir.setReturnValue(
                new ImmutableDelegatingMap<>(
                    "containers", String.class, ICurioStacksHandler.class,
                    holder.getSlotContainers(),
                    CuriosConversionUtils::slotConvertToC,
                    CuriosConversionUtils::slotConvertToA,
                    AccessoriesBasedStackHandler::new,
                    handler -> (handler instanceof AccessoriesBasedStackHandler(var container)) ? container : null
                )
            );
        }
    }

    @Inject(method = "replace", at = @At("HEAD"), cancellable = true, remap = false)
    private void replaceWithNoop(Map<String, ICurioStacksHandler> curios, CallbackInfo ci) {
        ci.cancel();
    }

    @WrapOperation(method = "serializeNBT(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;", at = @At(value = "FIELD", target = "Ltop/theillusivec4/curios/common/capability/CurioInventory;curios:Ljava/util/Map;"))
    private Map<String, ICurioStacksHandler> useMethodInsteadOfField(CurioInventory instance, Operation<Map<String, ICurioStacksHandler>> original) {
        return instance.asMap();
    }

    @ModifyReturnValue(method = "serializeNBT(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
    private CompoundTag addFlagForEncodedByCompat(CompoundTag original) {
        original.putBoolean("AccessoriesEncoded", true);

        return original;
    }
}
