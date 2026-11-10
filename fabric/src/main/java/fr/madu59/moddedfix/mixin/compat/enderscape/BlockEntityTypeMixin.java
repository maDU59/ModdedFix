package fr.madu59.moddedfix.mixin.compat.enderscape;

import net.bunten.enderscape.Enderscape;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.bunten.enderscape.registry.EnderscapeBlocks.*;

/**
 * Fixes Enderscape's BlockEntityType#isValid mixin.
 * EnderScape's blocks are no longer considered valid for every BlockEntityType, only for the correct one.
 */

@Mixin(value = BlockEntityType.class, priority = 2000)
public abstract class BlockEntityTypeMixin {

    @Unique
    private static final List<Identifier> moddedfix$VALID_WOOD_TYPES = List.of(
            Enderscape.id("veiled"),
            Enderscape.id("celestial"),
            Enderscape.id("murublight")
    );

    @Unique
    private static final List<Identifier> moddedfix$VALID_SHELVES = List.of(
            Enderscape.id("veiled_shelf"),
            Enderscape.id("celestial_shelf"),
            Enderscape.id("murublight_shelf")
    );

    @Inject(method = "isValid", at = @At("RETURN"), cancellable = true)
    private void isValid(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        BlockEntityType<?> self = (BlockEntityType<?>) (Object) this;
        Block block = state.getBlock();
        if (state.is(VOID_CAMPFIRE)) {
            if (self == BlockEntityType.CAMPFIRE) return;
            cir.setReturnValue(false);
        } else if (block instanceof ShelfBlock && moddedfix$VALID_SHELVES.contains(block.builtInRegistryHolder().key().identifier())) {
            if (self == BlockEntityType.SHELF) return;
            cir.setReturnValue(false);
        } else if (block instanceof SignBlock sign) {
            if ((block instanceof CeilingHangingSignBlock || block instanceof WallHangingSignBlock) && moddedfix$VALID_WOOD_TYPES.contains(Identifier.tryParse(sign.type().name()))) {
                if (self == BlockEntityType.HANGING_SIGN) return;
                cir.setReturnValue(false);
            } else if ((block instanceof StandingSignBlock || block instanceof WallSignBlock) && moddedfix$VALID_WOOD_TYPES.contains(Identifier.tryParse(sign.type().name()))) {
                if (self == BlockEntityType.SIGN) return;
                cir.setReturnValue(false);
            }
        }
        else if (state.is(END_TRIAL_SPAWNER)) {
            if(self == BlockEntityType.TRIAL_SPAWNER) return;
            cir.setReturnValue(false);
        }
        else if (state.is(END_VAULT)) {
            if(self == BlockEntityType.VAULT) return;
            cir.setReturnValue(false);
        }
    }
}
