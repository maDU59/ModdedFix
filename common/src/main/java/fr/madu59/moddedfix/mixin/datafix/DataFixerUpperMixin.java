package fr.madu59.moddedfix.mixin.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixerUpper;
import com.mojang.serialization.Dynamic;
import fr.madu59.moddedfix.datafix.ModdedFixDataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.fixes.References;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataFixerUpper.class)
public class DataFixerUpperMixin {
    @Inject(method = "update", at = @At("HEAD"))
    public <T> void moddedfix$update(DSL.TypeReference type, Dynamic<T> input, int version, int newVersion, CallbackInfoReturnable<Dynamic<T>> cir) {

        System.out.println("ModdedFix: remapping " + type + " data for version " + version + " to " + newVersion);

        if(type == References.CHUNK && input.getValue() instanceof CompoundTag chunkData) {
            System.out.println("ModdedFix: remapping chunk data for version " + version + " to " + newVersion);
            ModdedFixDataFixer.remapChunkBlocks(chunkData);
            ModdedFixDataFixer.remapChunkBlockEntities(chunkData);
        }
        else if(type == References.PLAYER && input.getValue() instanceof CompoundTag playerData) {
            System.out.println("ModdedFix: remapping player data for version " + version + " to " + newVersion);
            ModdedFixDataFixer.remapInventoryBlocks(playerData);
        }
    }
}
