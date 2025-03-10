package xyz.bluspring.valleyutils.mixin.fixes.yungscavebiomes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.yungnickyoung.minecraft.yungscavebiomes.block.PricklyVinesBlock;
import com.yungnickyoung.minecraft.yungscavebiomes.module.DamageTypeModule;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PricklyVinesBlock.class)
@IfModLoaded("yungscavebiomes")
public class PricklyVinesBlockMixin {
    @WrapOperation(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean tryFixDamageSource(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
        return original.call(instance, instance.damageSources().source(DamageTypeModule.PRICKLY_VINES), amount);
    }
}
