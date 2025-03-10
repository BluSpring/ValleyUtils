package xyz.bluspring.valleyutils.live

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import xyz.bluspring.valleyutils.ValleyUtils
import xyz.bluspring.valleyutils.live.detectors.TwitchDetector
import java.util.*
import kotlin.time.Duration.Companion.minutes

class ServerLiveManager(val server: MinecraftServer) : LiveManager() {
    private val timer = Timer()
    //val youtubeDetector = YoutubeDetector(this)
    val twitchDetector = TwitchDetector(this)

    init {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                try {
                    //youtubeDetector.manualScan()
                    twitchDetector.manualScan()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 0L, 2.minutes.inWholeMilliseconds)

        ServerPlayNetworking.registerGlobalReceiver(ValleyUtils.id("get_current_live")) { server, player, listener, buf, sender ->
            val size = buf.readVarInt()
            val list = mutableListOf<UUID>()
            for (i in 0 until size) {
                list.add(buf.readUUID())
            }

            this.validateLivePlayers(player, list)
        }

        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            var notLive = false

            /*youtubeDetector.updateForPlayer(handler.player, youtubeDetector.buildPackets().apply {
                if (this.second.contains(handler.player.uuid)) {
                    notLive = false
                    for (player in server.playerList.players) {
                        youtubeDetector.updateForPlayer(player, this)
                    }
                } else if (!notLive) {
                    notLive = true
                }
            })*/
            twitchDetector.updateForPlayer(handler.player, twitchDetector.buildPackets().apply {
                if (this.second.contains(handler.player.uuid)) {
                    notLive = false
                    for (player in server.playerList.players) {
                        twitchDetector.updateForPlayer(player, this)
                    }
                } else if (!notLive) {
                    notLive = true
                }
            })

            if (notLive) {
                val buf = PacketByteBufs.create()
                buf.writeUUID(handler.player.uuid)
                buf.writeBoolean(false)

                twitchDetector.updateForPlayer(handler.player, listOf(buf), listOf(handler.player.uuid))
            }

            ServerPlayNetworking.send(handler.player, ValleyUtils.id("get_current_live"), PacketByteBufs.empty())
        }

        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            val buf = PacketByteBufs.create()
            buf.writeUUID(handler.player.uuid)
            buf.writeBoolean(false)

            if (server.playerList == null)
                return@register

            for (player in server.playerList.players) {
                twitchDetector.updateForPlayer(player, listOf(buf), listOf(handler.player.uuid))
            }
        }
    }

    override fun onClose() {
        timer.cancel()
        //youtubeDetector.onClose()
    }

    fun validateLivePlayers(player: ServerPlayer, liveOnClient: List<UUID>) {
        if (liveOnClient.all { live.contains(it) })
            return

        for (uuid in liveOnClient.filter { !live.contains(it) }) {
            val buf = PacketByteBufs.create()
            buf.writeUUID(uuid)
            buf.writeBoolean(false)
            ServerPlayNetworking.send(player, ValleyUtils.id("set_live_status"), buf)
        }
    }
}