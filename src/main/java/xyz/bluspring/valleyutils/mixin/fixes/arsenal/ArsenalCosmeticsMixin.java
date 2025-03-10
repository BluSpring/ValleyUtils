package xyz.bluspring.valleyutils.mixin.fixes.arsenal;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.doctor4t.arsenal.cca.ArsenalComponents;
import dev.doctor4t.arsenal.index.ArsenalCosmetics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArsenalCosmetics.class)
@Pseudo
@IfModLoaded("arsenal")
public interface ArsenalCosmeticsMixin {
    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    private static void fixPortalCrash(ItemStack itemStack, CallbackInfoReturnable<String> cir) {
        if (!ArsenalComponents.WEAPON_OWNER_COMPONENT.isProvidedBy(itemStack)) {
            cir.setReturnValue("default");
        }
    }
}
