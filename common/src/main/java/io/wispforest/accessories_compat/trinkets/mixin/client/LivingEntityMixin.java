package io.wispforest.accessories_compat.trinkets.mixin.client;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories_compat.trinkets.pond.CosmeticLookupTogglable;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements CosmeticLookupTogglable {

    @Unique
    private boolean tclayer$cosmeticLookupToggle;

    @Override
    public void accessories$setLookupToggle(boolean value) {
        var capability = AccessoriesCapability.get((LivingEntity)(Object) this);

        if(capability == null) {
            this.tclayer$cosmeticLookupToggle = false;

            return;
        }

        this.tclayer$cosmeticLookupToggle = value;
    }

    @Override
    public boolean accessories$getLookupToggle() {
        if(!((LivingEntity)(Object) this).level().isClientSide()) return false;

        return tclayer$cosmeticLookupToggle;
    }
}
