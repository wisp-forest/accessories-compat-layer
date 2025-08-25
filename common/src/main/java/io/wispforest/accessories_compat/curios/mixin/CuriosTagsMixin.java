package io.wispforest.accessories_compat.curios.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.theillusivec4.curios.api.CuriosTags;

@Mixin(CuriosTags.class)
public abstract class CuriosTagsMixin {
    @WrapOperation(method = "createItemTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation accessories$adjustTagCreation(String namespace, String path, Operation<ResourceLocation> original) {
        var parts = path.split(":");

        return original.call(parts.length >= 2 ? parts[0] : namespace, path);
    }
}
