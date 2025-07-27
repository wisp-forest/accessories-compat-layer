package io.wispforest.accessories_compat.curios.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.Curios;
@Mixin(Curios.ClientProxy.class)
public abstract class CuriosClientProxyMixin {
    @Inject(method = "registerKeys", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preventKeyRegister(RegisterKeyMappingsEvent evt, CallbackInfo ci) {
        ci.cancel();
    }

    @WrapOperation(method = "setupClient", at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/api/IEventBus;register(Ljava/lang/Object;)V"), remap = false)
    private static void preventAllBusRegisters(IEventBus instance, Object object, Operation<Void> original) {
        // NO-OP
    }

    @Inject(method = "addPlayerLayer", at = @At(value = "HEAD"), cancellable = true)
    private static void preventAddingPlayerLayer(EntityRenderersEvent.AddLayers evt, PlayerSkin.Model model, CallbackInfo ci) {
        ci.cancel();
    }
}
