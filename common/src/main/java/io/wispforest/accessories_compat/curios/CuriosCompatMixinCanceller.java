package io.wispforest.accessories_compat.curios;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class CuriosCompatMixinCanceller implements MixinCanceller {
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (mixinClassName.contains("curios")) {
            if (mixinClassName.contains("MixinApplyBonusCount")
                || mixinClassName.contains("MixinEnchantedCountIncreaseFunction")
                || mixinClassName.contains("MixinInventory")
                || mixinClassName.contains("MixinLivingEntity")
                || mixinClassName.contains("MixinPiglinAi")
                || mixinClassName.contains("MixinPowderSnowBlock")) {
                return true;
            }
        }


        return false;
    }
}
