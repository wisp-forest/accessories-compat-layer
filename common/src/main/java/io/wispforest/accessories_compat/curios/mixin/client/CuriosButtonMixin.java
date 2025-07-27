package io.wispforest.accessories_compat.curios.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.client.AccessoriesClient;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.client.gui.CuriosButton;

@Mixin(CuriosButton.class)
public abstract class CuriosButtonMixin {
    @WrapOperation(method = "lambda$new$0", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/network/PacketDistributor;sendToServer(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;[Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;)V", ordinal = 1))
    private static void openAccessoriesScreenInstead(CustomPacketPayload otherPayload, CustomPacketPayload[] payload, Operation<Void> original) {
        AccessoriesClient.attemptToOpenScreen(false);
    }
}
