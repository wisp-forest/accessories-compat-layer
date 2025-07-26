package io.wispforest.accessories_compat.trinkets.mixin.owo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories_compat.common.AccessoriesCompatConfig;
import io.wispforest.accessories_compat.trinkets.utils.SlotIdRedirect;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.ReflectiveEndecBuilder;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.config.ConfigWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ConfigWrapper.class)
public abstract class ConfigWrapperMixin {

    @WrapOperation(method = "<init>(Ljava/lang/Class;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lio/wispforest/owo/serialization/endec/MinecraftEndecs;addDefaults(Lio/wispforest/endec/impl/ReflectiveEndecBuilder;)Lio/wispforest/endec/impl/ReflectiveEndecBuilder;"))
    private ReflectiveEndecBuilder accessories$addRequiredEndec(ReflectiveEndecBuilder builder, Operation<ReflectiveEndecBuilder> original) {
        original.call(builder);

        if (((ConfigWrapper)(Object)this) instanceof AccessoriesCompatConfig) {
            final Endec<SlotIdRedirect> ENDEC = StructEndecBuilder.of(
                    Endec.STRING.fieldOf("trinketsId", obj -> obj.trinketsId),
                    Endec.STRING.fieldOf("accessoriesId", obj -> obj.accessoriesId),
                    Endec.INT.fieldOf("additionalSlot", obj -> obj.additionalSlot),
                    SlotIdRedirect::new
            );

            builder.register(ENDEC, SlotIdRedirect.class);
        }

        return builder;
    }

}
