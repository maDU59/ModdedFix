package fr.madu59.moddedfix.mixin.compat.iris.at_midBlock_fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Backports #3328
 */

@Pseudo
@Mixin(value = IrisVertexFormats.class, remap = false)
public abstract class IrisVertexFormatsMixin {

    @Unique
    private static boolean moddedfix$shouldApply = false;

    @ModifyArg(
            method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexFormatElement;register(IILcom/mojang/blaze3d/vertex/VertexFormatElement$Type;Lcom/mojang/blaze3d/vertex/VertexFormatElement$Usage;I)Lcom/mojang/blaze3d/vertex/VertexFormatElement;",
                    ordinal = 4),
            index = 4
    )
    private static int moddedfix$midBlockCount(int original) {
        if(original != 3) return original;
        else{
            moddedfix$shouldApply = true;
            return 4;
        }
    }

    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexFormat$Builder;padding(I)Lcom/mojang/blaze3d/vertex/VertexFormat$Builder;",
                    ordinal = 1)
    )
    private static VertexFormat.Builder moddedfix$dropTrailingPadding(VertexFormat.Builder builder, int bytes, Operation<VertexFormat.Builder> original) {
        if(!moddedfix$shouldApply) return original.call(builder, bytes);
        return builder;
    }
}
