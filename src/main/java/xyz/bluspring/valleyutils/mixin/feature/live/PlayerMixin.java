package xyz.bluspring.valleyutils.mixin.feature.live;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.bluspring.valleyutils.ValleyUtils;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyReturnValue(method = "decorateDisplayNameComponent", at = @At("RETURN"))
    private MutableComponent addLiveComponent(MutableComponent original) {
        var liveManager = ValleyUtils.Companion.getLiveManager((Player) (Object) this);
        if (liveManager != null && liveManager.isLive((Player) (Object) this)) {
            return Component.empty()
                .append(
                    Component.literal("◆ ")
                        .withStyle(ChatFormatting.DARK_PURPLE)
                )
                .append(original);
        }

        return original;
    }
}
