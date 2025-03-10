package xyz.bluspring.valleyutils.mixin.fixes.mipmap;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.texture.SpriteLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {
    @Redirect(method = "stitch", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;log2(I)I", ordinal = 2))
    private int smpu$avoidMipmapLowering(int value, @Local(argsOnly = true) int mipLevel) {
        return mipLevel;
    }
}