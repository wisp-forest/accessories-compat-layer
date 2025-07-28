package io.wispforest.accessories_compat.curios.mixin;

import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.networking.server.NukeAccessories;
import io.wispforest.accessories.networking.server.SyncCosmeticToggle;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.network.client.CPacketOpenCurios;
import top.theillusivec4.curios.common.network.client.CPacketToggleRender;
import top.theillusivec4.curios.common.network.server.CuriosServerPayloadHandler;

@Mixin(CuriosServerPayloadHandler.class)
public abstract class CuriosServerPayloadHandlerMixin {
    @Inject(method = "lambda$handlerToggleRender$2", at = @At("HEAD"), cancellable = true, remap = false)
    private static void callIntoAccessoriesCosmeticToggle(IPayloadContext ctx, CPacketToggleRender data, CallbackInfo ci) {
        Player player = ctx.player();

        var packet = new SyncCosmeticToggle(ctx.player().getId(), CuriosConversionUtils.slotConvertToA(data.identifier()), data.index());

        SyncCosmeticToggle.handlePacket(packet, player);

        ci.cancel();
    }

    @Inject(method = "lambda$handleOpenCurios$6", at = @At("HEAD"), cancellable = true, remap = false)
    private static void callIntoAccessoriesScreenOpen(IPayloadContext ctx, CPacketOpenCurios data, CallbackInfo ci) {
        Player player = ctx.player();
        if (player instanceof ServerPlayer serverPlayer) {
            Accessories.askPlayerForVariant(serverPlayer);
        }
        ci.cancel();
    }

    @Inject(method = "lambda$handleDestroyPacket$11", at = @At("HEAD"), cancellable = true, remap = false)
    private static void callIntoAccessoriesNuking(IPayloadContext ctx, CallbackInfo ci) {
        Player player = ctx.player();
        NukeAccessories.handlePacket(new NukeAccessories(), player);
        ci.cancel();
    }
}
