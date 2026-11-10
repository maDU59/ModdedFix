package fr.madu59.moddedfix.mixin.compat.iris.mc_entity_fix;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import fr.madu59.moddedfix.compat.iris.mc_entity_fix.ShaderAttributeInputsExt;
import net.irisshaders.iris.gl.state.ShaderAttributeInputs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShaderAttributeInputs.class)
public abstract class ShaderAttributeInputsMixin implements ShaderAttributeInputsExt {
    @Unique
    private int moddedfix$entityComponents = 0;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void moddedfix$init(VertexFormat format, boolean isFullbright, boolean isLines, boolean glint, boolean text, boolean ie, CallbackInfo ci) {
        List<String> names = format.getElementAttributeNames();
        List<VertexFormatElement> elements = format.getElements();
        for (int index = 0; index < names.size(); index++) {
            VertexFormatElement element = elements.get(index);
            String name = names.get(index);

            if (element.type() == VertexFormatElement.Type.FLOAT) {
                continue;
            }

            if ("mc_Entity".equals(name)) {
                moddedfix$entityComponents = element.count();
            }
        }
    }

    @Inject(method = "hashCode", at = @At("RETURN"), cancellable = true)
    private void moddedfix$hashCode(CallbackInfoReturnable<Integer> cir, @Local(ordinal = 0) int prime) {
        cir.setReturnValue(prime * cir.getReturnValue() + moddedfix$entityComponents);
    }

    @Inject(method = "equals", at = @At("RETURN"), cancellable = true)
    private void moddedfix$equals(Object obj, CallbackInfoReturnable<Boolean> cir){
        if(!cir.getReturnValue() || obj == this) return;
        cir.setReturnValue(moddedfix$entityComponents == ((ShaderAttributeInputsExt)obj).moddedfix$getEntityComponents());
    }

    @Override
    public int moddedfix$getEntityComponents() {
        return moddedfix$entityComponents;
    }
}
