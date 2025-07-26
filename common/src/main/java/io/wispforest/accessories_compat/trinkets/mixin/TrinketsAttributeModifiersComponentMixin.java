package io.wispforest.accessories_compat.trinkets.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.emi.trinkets.api.TrinketsAttributeModifiersComponent;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.attributes.SlotAttribute;
import io.wispforest.accessories.utils.AttributeUtils;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.RegistriesAttribute;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(TrinketsAttributeModifiersComponent.class)
public abstract class TrinketsAttributeModifiersComponentMixin {

    @Mixin(TrinketsAttributeModifiersComponent.Entry.class)
    public abstract static class EntryMixin {
        @Unique
        private static final Endec<Holder<Attribute>> ATTRIBUTE_ENDEC = MinecraftEndecs.IDENTIFIER.xmapWithContext(
            (context, attributeType) -> {
                if(attributeType.getNamespace().equals(Accessories.MODID)) return SlotAttribute.getAttributeHolder(attributeType.getPath());

                return context.requireAttributeValue(RegistriesAttribute.REGISTRIES)
                    .registryManager()
                    .registryOrThrow(Registries.ATTRIBUTE)
                    .getHolder(attributeType)
                    .orElseThrow(IllegalStateException::new);
            },
            (context, attributeHolder) -> {
                var attribute = attributeHolder.value();

                if(attribute instanceof SlotAttribute slotAttribute) return Accessories.of(slotAttribute.slotName());

                return context.requireAttributeValue(RegistriesAttribute.REGISTRIES)
                    .registryManager()
                    .registryOrThrow(Registries.ATTRIBUTE)
                    .getKey(attribute);
            }
        );

        @Unique
        private static final Endec<TrinketsAttributeModifiersComponent.Entry> ENDEC = StructEndecBuilder.of(
            ATTRIBUTE_ENDEC.fieldOf("type", TrinketsAttributeModifiersComponent.Entry::attribute),
            AttributeUtils.ATTRIBUTE_MODIFIER_ENDEC.flatFieldOf(TrinketsAttributeModifiersComponent.Entry::modifier),
            Endec.STRING.optionalOf().fieldOf("slot", TrinketsAttributeModifiersComponent.Entry::slot),
            TrinketsAttributeModifiersComponent.Entry::new
        );

        @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", remap = false))
        private static Codec<TrinketsAttributeModifiersComponent.Entry> changeCodec(Function<RecordCodecBuilder.Instance<TrinketsAttributeModifiersComponent.Entry>, ? extends App<RecordCodecBuilder.Mu<TrinketsAttributeModifiersComponent.Entry>, TrinketsAttributeModifiersComponent.Entry>> builder, Operation<Codec<TrinketsAttributeModifiersComponent.Entry>> original) {
            return CodecUtils.toCodec(ENDEC);
        }

        @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function3;)Lnet/minecraft/network/codec/StreamCodec;"))
        private static <B, C, T1, T2, T3> StreamCodec<RegistryFriendlyByteBuf, TrinketsAttributeModifiersComponent.Entry> test(
            StreamCodec<? super B, T1> p_320928_,
            Function<C, T1> p_320123_,
            StreamCodec<? super B, T2> p_319815_,
            Function<C, T2> p_319965_,
            StreamCodec<? super B, T3> p_319834_,
            Function<C, T3> p_320645_,
            Function3<T1, T2, T3, C> p_320386_,
            Operation<StreamCodec<B, C>> original) {
            return CodecUtils.toPacketCodec(ENDEC);
        }
    }
}
