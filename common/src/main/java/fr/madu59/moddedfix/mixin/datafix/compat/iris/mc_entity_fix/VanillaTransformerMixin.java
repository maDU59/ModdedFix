package fr.madu59.moddedfix.mixin.datafix.compat.iris.mc_entity_fix;

import fr.madu59.moddedfix.compat.iris.mc_entity_fix.ShaderAttributeInputsExt;
import fr.madu59.moddedfix.compat.iris.mc_entity_fix.Transformer;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import net.irisshaders.iris.pipeline.transform.parameter.VanillaParameters;
import net.irisshaders.iris.pipeline.transform.transformer.VanillaTransformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaTransformer.class)
public abstract class VanillaTransformerMixin {
    @Inject(method = "transform", at = @At(value = "INVOKE", target = "Lio/github/douira/glsl_transformer/ast/query/Root;rename(Ljava/lang/String;Ljava/lang/String;)Z", ordinal = 0))
    private static void moddedfix$onTransform(ASTParser t, TranslationUnit tree, Root root, VanillaParameters parameters, CallbackInfo ci) {
        Transformer.patchIntegerAttribute(t, tree, root, "mc_Entity", "iris_Entity", ((ShaderAttributeInputsExt) parameters.inputs).moddedfix$getEntityComponents());
    }
}
