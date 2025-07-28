package io.wispforest.accessories_compat.curios.mixin.client;

import io.wispforest.accessories.networking.client.AccessoryBreak;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.network.client.CuriosClientPayloadHandler;
import top.theillusivec4.curios.common.network.server.SPacketBreak;

@Mixin(CuriosClientPayloadHandler.class)
public abstract class CuriosClientPayloadHandlerMixin {
    @Shadow
    private static void handle(IPayloadContext ctx, Runnable handler) {}

    @Inject(method = {
        "handle(Ltop/theillusivec4/curios/common/network/server/SPacketSetIcons;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
        "handle(Ltop/theillusivec4/curios/common/network/server/SPacketQuickMove;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
        "handle(Ltop/theillusivec4/curios/common/network/server/SPacketPage;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
        "handle(Ltop/theillusivec4/curios/common/network/server/sync/SPacketSyncRender;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
        "handle(Ltop/theillusivec4/curios/common/network/server/sync/SPacketSyncModifiers;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
        "handle(Ltop/theillusivec4/curios/common/network/server/sync/SPacketSyncData;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
        "handle(Ltop/theillusivec4/curios/common/network/server/sync/SPacketSyncCurios;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
        "handle(Ltop/theillusivec4/curios/common/network/server/SPacketGrabbedItem;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
        "handle(Ltop/theillusivec4/curios/common/network/server/sync/SPacketSyncStack;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
        "handle(Ltop/theillusivec4/curios/common/network/server/sync/SPacketSyncActiveState;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V"
    }, at = @At("HEAD"), cancellable = true, remap = false)
    private void preventPacketHandling(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "handle(Ltop/theillusivec4/curios/common/network/server/SPacketBreak;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V", at = @At("HEAD"), remap = false)
    private void callAccessoriesHandling(SPacketBreak data, IPayloadContext ctx, CallbackInfo ci) {
        handle(ctx, () -> {
            AccessoryBreak.handlePacket(new AccessoryBreak(data.entityId(), CuriosConversionUtils.slotConvertToA(data.curioId()), data.slotId()), ctx.player());
        });
    }
}
