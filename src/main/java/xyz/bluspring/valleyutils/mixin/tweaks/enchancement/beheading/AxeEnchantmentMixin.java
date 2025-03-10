package xyz.bluspring.valleyutils.mixin.tweaks.enchancement.beheading;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.arsenal.index.ArsenalItems;
import moriyashiine.enchancement.common.enchantment.AxeEnchantment;
import moriyashiine.enchancement.common.init.ModEnchantments;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AxeEnchantment.class)
public abstract class AxeEnchantmentMixin {
    @ModifyReturnValue(method = "canEnchant", at = @At("RETURN"))
    private boolean addBeheadingToScythe(boolean original, @Local(argsOnly = true) ItemStack stack) {
        return original || (stack.is(ArsenalItems.SCYTHE) && (Object) this == ModEnchantments.BEHEADING);
    }
}
