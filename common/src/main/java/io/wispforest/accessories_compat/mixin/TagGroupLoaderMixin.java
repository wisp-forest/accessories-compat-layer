package io.wispforest.accessories_compat.mixin;

import io.wispforest.accessories_compat.common.TagModificationLogic;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

/*
 * This mixin acts as a method to adjust tags BiDirectionally from Accessories and any compat modules added by compat layer
 */
@Mixin(value = TagLoader.class, priority = 2000)
public abstract class TagGroupLoaderMixin {

    @Final
    @Shadow
    private String directory;

    @Inject(method = "load", at = @At("TAIL"))
    public void attemptTagManipulation(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
        if (!Registries.tagsDirPath(BuiltInRegistries.ITEM.key()).equals(directory)) return;

        TagModificationLogic.modify(cir.getReturnValue());
    }
}

