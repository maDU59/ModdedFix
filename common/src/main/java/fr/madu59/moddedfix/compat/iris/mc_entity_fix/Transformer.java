package fr.madu59.moddedfix.compat.iris.mc_entity_fix;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.declaration.TypeAndInitDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinNumericTypeSpecifier;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import io.github.douira.glsl_transformer.util.Type;

/**
 * Backports #3263
 */

public class Transformer {
    public static void patchIntegerAttribute(
            ASTParser t,
            TranslationUnit tree,
            Root root,
            String name,
            String alias,
            int components) {
        if (components == 0) {
            return;
        }

        Type dimension = Type.BOOL;
        for (Identifier id : root.identifierIndex.get(name)) {
            TypeAndInitDeclaration initDeclaration = (TypeAndInitDeclaration) id.getAncestor(
                    2, 0, TypeAndInitDeclaration.class::isInstance);
            if (initDeclaration == null) {
                continue;
            }
            DeclarationExternalDeclaration declaration = (DeclarationExternalDeclaration) initDeclaration.getAncestor(
                    1, 0, DeclarationExternalDeclaration.class::isInstance);
            if (declaration == null) {
                continue;
            }
            if (initDeclaration.getType().getTypeSpecifier() instanceof BuiltinNumericTypeSpecifier numeric) {
                dimension = numeric.type;

                declaration.detachAndDelete();
                initDeclaration.detachAndDelete();
                id.detachAndDelete();
                break;
            }
        }

        // The pack never declared it, so there is nothing to redeclare.
        if (dimension == Type.BOOL) {
            return;
        }

        root.replaceReferenceExpressions(t, name, alias);

        String declaration = switch (dimension) {
            case FLOAT32 -> "float " + alias + " = float(" + name + ".x);";
            case INT32 -> "int " + alias + " = " + name + ".x;";
            case F32VEC2 -> "vec2 " + alias + " = vec2(" + swizzle(name, components, 2, true) + ");";
            case F32VEC3 -> "vec3 " + alias + " = vec3(" + swizzle(name, components, 3, true) + ");";
            case F32VEC4 -> "vec4 " + alias + " = vec4(" + swizzle(name, components, 4, true) + ");";
            case I32VEC2 -> "ivec2 " + alias + " = ivec2(" + swizzle(name, components, 2, false) + ");";
            case I32VEC3 -> "ivec3 " + alias + " = ivec3(" + swizzle(name, components, 3, false) + ");";
            case I32VEC4 -> "ivec4 " + alias + " = ivec4(" + swizzle(name, components, 4, false) + ");";
            default -> throw new IllegalStateException(
                    "Got an invalid format for " + name + " (" + dimension.getCompactName() + ").");
        };

        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS, declaration);
        tree.parseAndInjectNode(t, ASTInjectionPoint.BEFORE_DECLARATIONS,
                "in ivec" + components + " " + name + ";");
    }

    // Pads the attribute out to the width the pack declared, matching how SodiumTransformer
    // pads mc_Entity: zero, with one in the fourth slot.
    private static String swizzle(String name, int available, int declared, boolean floating) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < declared; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            if (i < available) {
                builder.append(name).append('.').append("xyzw".charAt(i));
            } else if (i == 3) {
                builder.append(floating ? "1.0" : "1");
            } else {
                builder.append(floating ? "0.0" : "0");
            }
        }
        return builder.toString();
    }
}
