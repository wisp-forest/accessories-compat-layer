package io.wispforest.accessories_compat.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class AccessoriesCompatMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(mixinClassName.contains("DataLoaderBase")) {
            return testForClass("io/wispforest/accessories/fabric/AccessoriesFabric");
        }

        if(mixinClassName.contains("AccessoriesForgeMixin")) {
            return testForClass(targetClassName);
        }

        return true;
    }

    public boolean testForClass(String targetClassName)  {
        try {
            MixinService.getService().getBytecodeProvider().getClassNode(targetClassName);

            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return ""; }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return List.of(); }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
