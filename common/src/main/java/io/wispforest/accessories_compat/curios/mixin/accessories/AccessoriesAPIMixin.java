//package io.wispforest.accessories_compat.curios.mixin.accessories;
//
//import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//import io.wispforest.accessories.api.AccessoriesAPI;
//import io.wispforest.accessories.api.data.AccessoriesTags;
//import net.minecraft.tags.TagKey;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import top.theillusivec4.curios.api.CuriosTags;
//
//@Mixin(AccessoriesAPI.class)
//public class AccessoriesAPIMixin {
//    @WrapOperation(method = "lambda$static$4", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 1))
//    private static boolean excludeValuesIfDesired(ItemStack instance, TagKey<Item> tag, Operation<Boolean> original) {
//        if (tag == AccessoriesTags.ANY_TAG && original.call(instance, CuriosTags.GENERIC_EXCLUSIONS)) {
//            return false;
//        }
//
//        return original.call(instance, tag);
//    }
//}
