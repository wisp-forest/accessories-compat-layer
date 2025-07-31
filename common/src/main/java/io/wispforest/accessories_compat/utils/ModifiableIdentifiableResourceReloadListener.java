package io.wispforest.accessories_compat.utils;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public record ModifiableIdentifiableResourceReloadListener(ResourceLocation location, PreparableReloadListener listener,
                                                    Set<ResourceLocation> dependencies) implements IdentifiableResourceReloadListener {

    public ModifiableIdentifiableResourceReloadListener(ResourceLocation location, PreparableReloadListener listener, ResourceLocation... dependencies) {
        this(location, listener, new HashSet<>(List.of(dependencies)));
    }

    @Override
    public ResourceLocation getFabricId() {
        return this.location;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return this.listener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
    }

    @Override
    public Collection<ResourceLocation> getFabricDependencies() {
        return dependencies;
    }
}
