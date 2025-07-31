package io.wispforest.accessories_compat.curios.wrapper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public record RenderLayerParentDummy<T extends LivingEntity>(EntityModel<T> getModel) implements RenderLayerParent<T, EntityModel<T>> {
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return ResourceLocation.withDefaultNamespace("");
    }
}
