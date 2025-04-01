package xyz.bluspring.valleyutils.mixin.tweaks.old_dimension;

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

    @ModifyArg(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
    private ResourceKey<Level> useOldDimension(ResourceKey<Level> dimension) {
        if (dimension == Level.OVERWORLD && this.level().dimension() == ValleyUtils.OLD_NETHER)
            return ValleyUtils.OLD_OVERWORLD;
        else if (dimension == Level.NETHER && this.level().dimension() == ValleyUtils.OLD_OVERWORLD)
            return ValleyUtils.OLD_NETHER;

        return dimension;
    }
}
