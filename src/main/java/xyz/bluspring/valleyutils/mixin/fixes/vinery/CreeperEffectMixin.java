package xyz.bluspring.valleyutils.mixin.fixes.vinery;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.vinery.core.effect.CreeperEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreeperEffect.class)
@IfModLoaded("vinery")
public class CreeperEffectMixin {
    @Redirect(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setHealth(F)V"))
    private void useHurtFunctionForDeath(ServerPlayer instance, float v) {
        instance.hurt(instance.damageSources().explosion(instance, instance), (instance.getMaxHealth() + instance.getArmorValue()) * 50);
    }

}
