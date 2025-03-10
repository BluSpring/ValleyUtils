package xyz.bluspring.valleyutils.mixin.fixes.yungscavebiomes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.yungnickyoung.minecraft.yungscavebiomes.block.IcicleBlock;
import com.yungnickyoung.minecraft.yungscavebiomes.module.DamageTypeModule;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IcicleBlock.class)
@IfModLoaded("yungscavebiomes")
public class IcicleBlockMixin {
    @ModifyReturnValue(method = "getFallDamageSource", at = @At("RETURN"))
    private DamageSource tryFixDamageSource(DamageSource original, @Local(argsOnly = true) Entity entity) {
        return entity.damageSources().source(DamageTypeModule.FALLING_ICICLE);
    }
}

