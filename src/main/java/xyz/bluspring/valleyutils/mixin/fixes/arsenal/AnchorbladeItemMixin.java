package xyz.bluspring.valleyutils.mixin.fixes.arsenal;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.arsenal.item.AnchorbladeItem;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AnchorbladeItem.class)
public class AnchorbladeItemMixin {
    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 1))
    private InteractionHand fixAnchorbladeRecall(InteractionHand par1, @Local(argsOnly = true) InteractionHand hand) {
        return hand;
    }
}
