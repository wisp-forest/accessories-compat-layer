package io.wispforest.accessories_compat.curios.pond;

import io.wispforest.accessories.impl.AccessoriesCapabilityImpl;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;

public interface CurioInventoryCapabilityExtension {
    static void attemptConversion(AccessoriesCapabilityImpl capability) {
        if (!((IAttachmentHolder) capability.entity()).hasData(CuriosRegistry.INVENTORY)) return;

        new CurioInventoryCapability(capability.entity());
    }

    AccessoriesCapabilityImpl capability();
}
