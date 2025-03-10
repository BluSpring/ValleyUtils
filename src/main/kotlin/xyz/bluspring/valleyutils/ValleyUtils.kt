package xyz.bluspring.valleyutils

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import xyz.bluspring.valleyutils.client.ValleyUtilsClient
import xyz.bluspring.valleyutils.live.LiveManager
import xyz.bluspring.valleyutils.live.ServerLiveManager
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue

class ValleyUtils : ModInitializer {

    override fun onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(HAS_VALLEY_UTILS_PACKET) { server, player, handler, buf, sender ->
            hasValleyUtilsPlayers.add(player)
        }

        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            hasValleyUtilsPlayers.remove(handler.player)
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            hasValleyUtilsPlayers.clear()
        }

        ServerLifecycleEvents.SERVER_STARTING.register {
            liveManager = ServerLiveManager(it)
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            liveManager?.onClose()
        }
    }

    companion object {
        const val MOD_ID = "lavender_valley"
        val hasValleyUtilsPlayers = ConcurrentLinkedQueue<Player>()

        var liveManager: LiveManager? = null

        fun getLiveManager(player: Player): LiveManager? {
            return if (player.level().isClientSide())
                ValleyUtilsClient.liveManager
            else
                liveManager
        }

        @JvmStatic
        fun id(name: String): ResourceLocation {
            ByteBuffer.wrap(byteArrayOf())
            return ResourceLocation(MOD_ID, name)
        }


        @JvmField val HAS_VALLEY_UTILS_PACKET = id("has_valley_utils")
    }
}
