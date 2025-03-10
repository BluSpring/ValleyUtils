package xyz.bluspring.valleyutils.live

import net.minecraft.world.entity.player.Player
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

abstract class LiveManager {
    val live = ConcurrentSkipListSet<UUID>()

    open fun isLive(player: Player): Boolean {
        return live.contains(player.uuid)
    }

    open fun onClose() {}
}