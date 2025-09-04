package io.wispforest.accessories_compat.curios.pond;

import io.wispforest.accessories.impl.AccessoriesHolderImpl;
import io.wispforest.accessories_compat.curios.wrapper.CuriosConversionUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;
import java.util.Map;

public interface CurioInventoryExtension {

    List<ItemStack> getInvalidStacks();

    Map<String, ICurioStacksHandler> getCuriosSlotView(boolean sorted);
}
