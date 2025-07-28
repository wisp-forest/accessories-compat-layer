package io.wispforest.accessories_compat.curios.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.impl.AccessoriesCapabilityImpl;
import io.wispforest.accessories_compat.curios.pond.CurioInventoryCapabilityExtension;
import io.wispforest.accessories_compat.curios.pond.CurioInventoryExtension;
import io.wispforest.accessories_compat.curios.wrapper.AccessoriesBasedStackHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.capability.CurioInventory;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;

import java.util.Set;
import java.util.stream.Collectors;

@Mixin(CurioInventoryCapability.class)
public abstract class CurioInventoryCapabilityMixin implements CurioInventoryCapabilityExtension {

    @Shadow @Final private CurioInventory curioInventory;
    @Shadow @Final private LivingEntity livingEntity;

    @Shadow public abstract void reset();

    @Unique
    private AccessoriesCapabilityImpl capability = null;

    @Override
    public AccessoriesCapabilityImpl capability() {
        return capability;
    }

    @ModifyExpressionValue(method = "<init>", at = @At(value = "FIELD", target = "Ltop/theillusivec4/curios/common/capability/CurioInventory;markDeserialized:Z"))
    private boolean initCapabilityFromEntity(boolean original, @Local(argsOnly = true) LivingEntity livingEntity) {
        if (CurioInventoryCapabilityExtension.CURRENTLY_CONVERTING.contains(livingEntity)) {
            return false;
        } else {
            var capability = AccessoriesCapability.get(livingEntity);

            if (capability == null) {
                throw new NullPointerException("Unable to create the CurioInventoryCapability due to the given AccessoriesCapability being null for the given entity! [Entity: " + livingEntity + "]");
            }

            this.capability = (AccessoriesCapabilityImpl) capability;

            return true;
        }
    }

    @Override
    public void capability(AccessoriesCapabilityImpl capability) {
        this.capability = capability;

        this.reset();
    }

    /**
     * @author blodhgarm
     * @reason use getInvalidStacks instead of invalidStacks
     */
    @Overwrite
    public void loseInvalidStack(ItemStack stack) {
        ((CurioInventoryExtension) (this.curioInventory)).getInvalidStacks().add(stack);
    }

    /**
     * @author blodhgarm
     * @reason use getInvalidStacks instead of invalidStacks
     */
    @Overwrite(remap = false)
    public void handleInvalidStacks() {
        if (this.livingEntity != null && !((CurioInventoryExtension) (this.curioInventory)).getInvalidStacks().isEmpty()) {
            if (this.livingEntity instanceof Player player) {
                ((CurioInventoryExtension) (this.curioInventory)).getInvalidStacks().forEach(
                    drop -> ItemHandlerHelper.giveItemToPlayer(player, drop));
            } else {
                ((CurioInventoryExtension) (this.curioInventory)).getInvalidStacks().forEach(
                    drop -> {
                        ItemEntity ent = this.livingEntity.spawnAtLocation(drop, 1.0F);
                        RandomSource rand = this.livingEntity.getRandom();
                        if (ent != null) {
                            ent.setDeltaMovement(
                                ent.getDeltaMovement()
                                    .add(
                                        (rand.nextFloat() - rand.nextFloat()) * 0.1F,
                                        rand.nextFloat() * 0.05F,
                                        (rand.nextFloat() - rand.nextFloat()) * 0.1F));
                        }
                    });
            }
            ((CurioInventoryExtension) (this.curioInventory)).getInvalidStacks().clear();
        }
    }

    /**
     * @author blodhgarm
     * @reason update accessories instead
     */
    @Overwrite(remap = false)
    public Set<ICurioStacksHandler> getUpdatingInventories() {
        return ((AccessoriesCapabilityImpl) this.capability.getHolder()).getUpdatingInventories().keySet()
            .stream()
            .map(container -> new AccessoriesBasedStackHandler(container))
            .collect(Collectors.toUnmodifiableSet());
    }
}
