package io.wispforest.accessories_compat.curios.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.client.DefaultAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories_compat.curios.wrapper.RenderLayerParentDummy;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(CuriosRendererRegistry.class)
public abstract class CuriosRendererRegistryMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void registerCuriosRenderersWithinAccessories(Item item, Supplier<ICurioRenderer> renderer, CallbackInfo ci) {
        AccessoriesRendererRegistry.registerRenderer(item, () -> {
            var curiosRenderer = renderer.get();

            return new AccessoryRenderer() {
                @Override
                public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                    curiosRenderer.render(
                        stack,
                        CuriosConversionUtils.objectsConvertToC(reference),
                        matrices,
                        new RenderLayerParentDummy<>(model),
                        multiBufferSource,
                        light,
                        limbSwing,
                        limbSwingAmount,
                        partialTicks,
                        ageInTicks,
                        netHeadYaw,
                        headPitch
                    );
                }
            };
        });
    }

    @ModifyReturnValue(method = "getRenderer", at = @At("RETURN"))
    private static Optional<ICurioRenderer> getAccessoryRendererIfEmpty(Optional<ICurioRenderer> original, @Local(argsOnly = true) Item item) {
        return original.or(() -> {
            return Optional.ofNullable(AccessoriesRendererRegistry.getRenderer(item)).flatMap(accessoryRenderer -> {
                if(accessoryRenderer == DefaultAccessoryRenderer.INSTANCE && !Accessories.config().clientOptions.forceNullRenderReplacement()) {
                    return Optional.empty();
                }

                return Optional.of(
                    new ICurioRenderer() {
                        @Override
                        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                            accessoryRenderer.render(
                                stack,
                                CuriosConversionUtils.convertToA(slotContext),
                                matrixStack,
                                renderLayerParent.getModel(),
                                renderTypeBuffer,
                                light,
                                limbSwing,
                                limbSwingAmount,
                                partialTicks,
                                ageInTicks,
                                netHeadYaw,
                                headPitch
                            );
                        }
                    }
                );
            });
        });
    }
}

