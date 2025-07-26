package io.wispforest.accessories_compat.trinkets.mixin.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import io.wispforest.accessories.api.events.OnDropCallback;
import io.wispforest.accessories.impl.event.WrappedEvent;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketsWrappingUtils;
import net.fabricmc.fabric.api.event.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(TrinketDropCallback.class)
public interface TrinketDropCallbackMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/EventFactory;createArrayBacked(Ljava/lang/Class;Ljava/util/function/Function;)Lnet/fabricmc/fabric/api/event/Event;"), remap = false)
    private static <T> Event<TrinketDropCallback> useWrappedEventInstead(Class<? super T> type, Function<T[], T> invokerFactory, Operation<Event<T>> original) {
        return new WrappedEvent<>(OnDropCallback.EVENT, callback -> {
            return (dropRule, stack, reference, damageSource) -> {
                var slotReference = TrinketsWrappingUtils.createTrinketsReference(reference);

                if(slotReference.isEmpty()) return io.wispforest.accessories.api.DropRule.DEFAULT;

                return TrinketsWrappingUtils.convertDropRule(callback.drop(TrinketsWrappingUtils.convertDropRule(dropRule), stack, slotReference.get(), reference.entity()));
            };
        }, onDropCallbackEvent -> {
            return (rule, stack, ref, entity) -> {
                var reference = TrinketsWrappingUtils.createAccessoriesReference(ref).get();
                var accessoryRule = TrinketsWrappingUtils.convertDropRule(rule);

                var source = entity.getLastDamageSource();

                if(source == null) source = entity.level().damageSources().genericKill();

                var value = onDropCallbackEvent.invoker().onDrop(accessoryRule, stack, reference, source);

                return value != null ? TrinketsWrappingUtils.convertDropRule(value) : TrinketEnums.DropRule.DEFAULT;
            };
        });
    }
}
