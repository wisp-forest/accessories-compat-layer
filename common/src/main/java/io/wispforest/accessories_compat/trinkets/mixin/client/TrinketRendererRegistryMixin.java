package io.wispforest.accessories_compat.trinkets.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.client.DefaultAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketsWrappingUtils;
import io.wispforest.accessories_compat.trinkets.wrapper.WrappedTrinketInventory;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(TrinketRendererRegistry.class)
public abstract class TrinketRendererRegistryMixin {

    @Inject(method = "registerRenderer", at = @At("TAIL"))
    private static void registerTrinketsRendererAsAccessories(Item item, TrinketRenderer trinketRenderer, CallbackInfo ci) {
        AccessoriesRendererRegistry.registerRenderer(item,
            () -> new AccessoryRenderer(){
                @Override
                public <M extends LivingEntity> void render(ItemStack stack, SlotReference ref, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                    matrices.pushPose();

                    var reference = TrinketsWrappingUtils.createTrinketsReference(ref, true);

                    if(reference.isEmpty()) return;

                    trinketRenderer.render(stack, reference.get(), model, matrices, multiBufferSource, light, ref.entity(), limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);

                    matrices.popPose();
                }
            }
        );
    }

    @ModifyReturnValue(method = "getRenderer", at = @At("RETURN"))
    private static Optional<TrinketRenderer> getAccessoryRendererIfEmpty(Optional<TrinketRenderer> original, @Local(argsOnly = true) Item item) {
        return original.or(() -> {
            return Optional.ofNullable(AccessoriesRendererRegistry.getRenderer(item)).flatMap(accessoryRenderer -> {
                if(accessoryRenderer == DefaultAccessoryRenderer.INSTANCE && !Accessories.config().clientOptions.forceNullRenderReplacement()) {
                    return Optional.empty();
                }

                return Optional.of(
                    (stack, ref, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {
                        var slotName = ((WrappedTrinketInventory) ref.inventory()).container.getSlotName();

                        var reference = SlotReference.of(entity, slotName, ref.index());

                        accessoryRenderer.render(stack, reference, matrices, contextModel, vertexConsumers, light, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
                    }
                );
            });
        });
    }
}
