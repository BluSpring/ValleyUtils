package xyz.bluspring.valleyutils.mixin.tweaks.old_dimension;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.bluspring.valleyutils.ValleyUtils;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Level level();

    @ModifyExpressionValue(method = "findDimensionEntryPoint", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;dimension()Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> addOldDimensionHandling(ResourceKey<Level> original) {
        if (this.level().dimension() == ValleyUtils.OLD_END)
            return Level.END;
        else if (this.level().dimension() == ValleyUtils.OLD_NETHER)
            return Level.NETHER;
        else if (this.level().dimension() == ValleyUtils.OLD_OVERWORLD)
            return Level.OVERWORLD;

        return original;
    }

    @ModifyExpressionValue(method = "findDimensionEntryPoint", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;dimension()Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> addOldDimensionHandling2(ResourceKey<Level> original) {
        if (this.level().dimension() == ValleyUtils.OLD_END)
            return Level.END;
        else if (this.level().dimension() == ValleyUtils.OLD_NETHER)
            return Level.NETHER;
        else if (this.level().dimension() == ValleyUtils.OLD_OVERWORLD)
            return Level.OVERWORLD;

        return original;
    }

    @ModifyArg(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
    private ResourceKey<Level> useOldDimension(ResourceKey<Level> dimension) {
        if (this.level().dimension() == ValleyUtils.OLD_NETHER)
            return ValleyUtils.OLD_OVERWORLD;
        else if (this.level().dimension() == ValleyUtils.OLD_OVERWORLD)
            return ValleyUtils.OLD_NETHER;

        return dimension;
    }
}
