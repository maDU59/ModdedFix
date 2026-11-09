package fr.madu59.moddedfix;

import fr.madu59.moddedfix.platform.Services;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ModdedFixMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".compat.")) {
            List<String> parts = List.of(mixinClassName.split("\\.compat\\."));
            if(parts.size() != 2) {
                return true;
            }
            parts = List.of(parts.getLast().split("\\."));
            String modId = parts.getFirst();
            return Services.PLATFORM.isModLoaded(modId);
        }
        return true;
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String target, ClassNode targetClass, String mixin, IMixinInfo mixinInfo) {}
    @Override public void postApply(String target, ClassNode targetClass, String mixin, IMixinInfo mixinInfo) {}
}