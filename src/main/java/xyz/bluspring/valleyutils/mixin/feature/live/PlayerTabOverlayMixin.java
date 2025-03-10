package xyz.bluspring.valleyutils.mixin.feature.live;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
    /*@ModifyReturnValue(method = "getNameForDisplay", at = @At("RETURN"))
    private Component addLiveComponent(Component original) {
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
    }*/
}
