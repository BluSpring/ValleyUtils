package xyz.bluspring.valleyutils.mixin.fixes.deflect;

import net.chance.deflect.procedures.ArrowIndactorProcedure;
import net.minecraft.world.InteractionResultHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArrowIndactorProcedure.class)
public class ArrowIndactorProcedureMixin {
    @Redirect(method = "lambda$new$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResultHolder;fail(Ljava/lang/Object;)Lnet/minecraft/world/InteractionResultHolder;"))
    private static <T> InteractionResultHolder<T> fixRightClickInteraction(T type) {
        return InteractionResultHolder.pass(type);
    }
}
