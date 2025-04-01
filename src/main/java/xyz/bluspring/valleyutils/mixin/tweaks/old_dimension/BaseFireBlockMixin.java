package xyz.bluspring.valleyutils.mixin.tweaks.old_dimension;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.bluspring.valleyutils.ValleyUtils;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {
    @ModifyReturnValue(method = "inPortalDimension", at = @At("RETURN"))
    private static boolean addAdditionalDimensions(boolean original, @Local(argsOnly = true) Level level) {
        return original || level.dimension() == ValleyUtils.OLD_OVERWORLD || level.dimension() == ValleyUtils.OLD_NETHER;
    }
}
