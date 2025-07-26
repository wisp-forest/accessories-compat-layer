package io.wispforest.accessories_compat.trinkets.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.trinkets.TrinketsMain;
import dev.emi.trinkets.api.LivingEntityTrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.wispforest.accessories_compat.trinkets.wrapper.WrappedTrinketComponent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.world.entity.LivingEntity;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TrinketsMain.class)
public abstract class TrinketsMainMixin {
    @WrapOperation(method = "onInitialize", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/resource/ResourceManagerHelper;registerReloadListener(Lnet/fabricmc/fabric/api/resource/IdentifiableResourceReloadListener;)V") , remap = false)
    private void preventRegister(ResourceManagerHelper instance, IdentifiableResourceReloadListener identifiableResourceReloadListener, Operation<Void> original) {
        // NO-OP
    }

    @WrapOperation(method = "onInitialize", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/Event;register(Ljava/lang/Object;)V"), remap = false)
    private <T> void preventEventRegister(Event<T> instance, T t, Operation<Void> original) {
        if (instance == UseItemCallback.EVENT) return;

        original.call(instance, t);
    }

    /**
     * @author blodhgarm
     * @reason Create wrapped versions instead
     */
    @Overwrite(remap = false)
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, TrinketsApi.TRINKET_COMPONENT, WrappedTrinketComponent::new);
        registry.registerForPlayers(TrinketsApi.TRINKET_COMPONENT, WrappedTrinketComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
