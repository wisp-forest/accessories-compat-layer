package io.wispforest.accessories_compat.trinkets.mixin.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.trinkets.api.event.TrinketEquipCallback;
import dev.emi.trinkets.api.event.TrinketUnequipCallback;
import io.wispforest.accessories.api.events.AccessoryChangeCallback;
import io.wispforest.accessories.api.events.SlotStateChange;
import io.wispforest.accessories.impl.event.WrappedEvent;
import io.wispforest.accessories_compat.trinkets.wrapper.TrinketsWrappingUtils;
import net.fabricmc.fabric.api.event.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(TrinketUnequipCallback.class)
public interface TrinketUnequipCallbackMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/EventFactory;createArrayBacked(Ljava/lang/Class;Ljava/util/function/Function;)Lnet/fabricmc/fabric/api/event/Event;"), remap = false)
    private static <T> Event<TrinketUnequipCallback> useWrappedEventInstead(Class<? super T> type, Function<T[], T> invokerFactory, Operation<Event<T>> original) {
        return new WrappedEvent<>(AccessoryChangeCallback.EVENT, callback -> {
            return (prevStack, currentStack, reference, stateChange) -> {
                var slotReference = TrinketsWrappingUtils.createTrinketsReference(reference);

                if(slotReference.isEmpty()) return;

                callback.onUnequip(currentStack, slotReference.get(), reference.entity());
            };
        }, accessoryChangeCallbackEvent -> (stack, slot, entity) -> {
            var ref = TrinketsWrappingUtils.createAccessoriesReference(slot).get();

            accessoryChangeCallbackEvent.invoker().onChange(stack, ref.getStack(), ref, SlotStateChange.REPLACEMENT);
        });
    }
}
