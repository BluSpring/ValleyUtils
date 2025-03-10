package xyz.bluspring.valleyutils.mixin.fixes.enchancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import moriyashiine.enchancement.client.render.entity.BrimstoneEntityRenderer;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrimstoneEntityRenderer.class)
@Pseudo
@IfModLoaded("iris")
public abstract class BrimstoneEntityRendererMixin {
    @WrapOperation(method = "render(Lmoriyashiine/enchancement/common/entity/projectile/BrimstoneEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;dragonExplosionAlpha(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"))
    private RenderType useCutoutIfIrisActive(ResourceLocation resourceLocation, Operation<RenderType> original) {
        if (IrisApi.getInstance().isShaderPackInUse()) {
            return RenderType.entityCutoutNoCull(resourceLocation);
        }

        return original.call(resourceLocation);
    }
}
