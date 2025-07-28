package io.wispforest.accessories_compat.curios.pond;

import io.wispforest.accessories.impl.AccessoriesCapabilityImpl;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.common.capability.CurioInventoryCapability;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public interface CurioInventoryCapabilityExtension {
    Set<LivingEntity> CURRENTLY_CONVERTING = Collections.newSetFromMap(WeakHashMap.newWeakHashMap(16));

    static void attemptConversion(AccessoriesCapabilityImpl capability) {
        var entity = capability.entity();

        if (!((IAttachmentHolder) entity).hasData(CuriosRegistry.INVENTORY)) return;

        CURRENTLY_CONVERTING.add(entity);

        ((CurioInventoryCapabilityExtension) new CurioInventoryCapability(entity)).capability(capability);

        CURRENTLY_CONVERTING.remove(entity);
    }

    AccessoriesCapabilityImpl capability();

    void capability(AccessoriesCapabilityImpl capability);
}
