package io.wispforest.accessories_compat.mixin;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories_compat.api.ModCompatibilityModule;
import io.wispforest.accessories_compat.api.tags.SlotTypesModifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.intellij.lang.annotations.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = SlotTypeLoader.class, priority = 1100)
public abstract class SlotTypeLoaderMixin {

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Ljava/util/HashMap;<init>()V", shift = At.Shift.AFTER, ordinal = 2))
    private void injectTrinketSpecificSlots(Map<Identifier, JsonObject> data, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci, @Local(name = "builders") HashMap<String, SlotTypeLoader.SlotBuilder> builders){
        var modifier = SlotTypesModifier.of(builders);

        for (var value : ModCompatibilityModule.getModules().values()) {
            value.addSlotTypes(modifier);
        }
    }
}
