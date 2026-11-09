package fr.madu59.moddedfix.mixin.datafix;

import fr.madu59.moddedfix.datafix.ModdedFixDataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleRegionStorage.class)
public class SimpleRegionStorageMixin {
    @Inject(method = "upgradeChunkTag", at = @At("HEAD"))
    public void moddedfix$upgradeChunkTag(CompoundTag chunkData, int version, CompoundTag p_459066_, CallbackInfoReturnable<CompoundTag> cir) {
        ModdedFixDataFixer.remapChunkBlocks(chunkData);
        ModdedFixDataFixer.remapChunkBlockEntities(chunkData);
    }
}
