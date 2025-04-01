package xyz.bluspring.valleyutils.mixin.tweaks.old_dimension;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.bluspring.valleyutils.ValleyUtils;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void avoidDimensionTeleport(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (level.dimension().equals(Level.OVERWORLD)) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
    private ResourceKey<Level> checkOldDimension(ResourceKey<Level> value, @Local(argsOnly = true) Level level) {
        if (level.dimension().location().equals(ValleyUtils.id("old_overworld")))
            return ResourceKey.create(Registries.DIMENSION, ValleyUtils.id("old_end"));
        else if (level.dimension().location().equals(ValleyUtils.id("old_end")))
            return ResourceKey.create(Registries.DIMENSION, ValleyUtils.id("old_overworld"));

        return value;
    }
}
