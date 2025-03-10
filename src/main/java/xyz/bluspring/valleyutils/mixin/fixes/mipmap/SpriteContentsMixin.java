package xyz.bluspring.valleyutils.mixin.fixes.mipmap;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpriteContents.class, priority = 990)
public class SpriteContentsMixin {
    @Shadow @Final private int width;

    @Shadow @Final private int height;

    @Inject(method = "upload", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/NativeImage;upload(IIIIIIIZZ)V", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void smpu$skipUploadIfInvalidMipLevel(int x, int y, int frameX, int frameY, NativeImage[] atlasData, CallbackInfo ci, @Local(ordinal = 4) int i) {
        if ((this.width >> i) <= 0 || (this.height >> i) <= 0)
            ci.cancel();
    }

    @Mixin(targets = "net.minecraft.client.renderer.texture.SpriteContents$InterpolationData")
    public static class InterpolationDataMixin {
        @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(IIZ)Lcom/mojang/blaze3d/platform/NativeImage;"))
        private NativeImage smpu$guardFromInvalidTexSize(int width, int height, boolean useCalloc, Operation<NativeImage> original) {
            return original.call(Math.max(1, width), Math.max(1, height), useCalloc);
        }
    }
}