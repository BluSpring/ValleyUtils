package xyz.bluspring.valleyutils.live.detectors

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.server.level.ServerPlayer
import org.slf4j.LoggerFactory
import xyz.bluspring.valleyutils.ValleyUtils
import xyz.bluspring.valleyutils.live.ServerLiveManager
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

abstract class LiveDetector(val manager: ServerLiveManager) {
    val isLive = ConcurrentSkipListSet<String>()
    val server = manager.server

    protected val usernameToUUID = Object2ObjectOpenHashMap<String, UUID>()
    protected val logger = LoggerFactory.getLogger(LiveDetector::class.java)

    abstract fun manualScan()
    fun update(isLive: Collection<String>, isNotLive: Collection<String>) {
        if (isLive.isEmpty() && isNotLive.isEmpty())
            return

        this.isLive.removeAll(isNotLive.toSet())
        this.isLive.addAll(isLive)

        val packets = buildPackets(isLive, isNotLive)

        if (server.playerList == null)
            return

        for (player in server.playerList.players) {
            updateForPlayer(player, packets)
        }
    }

    fun buildPackets(isLive: Collection<String>, isNotLive: Collection<String>): Pair<List<FriendlyByteBuf>, List<UUID>> {
        val packets = mutableListOf<FriendlyByteBuf>()
        val uuids = mutableListOf<UUID>()

        for (live in isLive) {
            val uuid = usernameToUUID[live] ?: throw IllegalStateException()

            val buf = PacketByteBufs.create()
            buf.writeUUID(uuid)
            buf.writeBoolean(true)
            packets.add(buf)
            uuids.add(uuid)
        }
        manager.live.addAll(uuids)

        for (live in isNotLive) {
            val uuid = usernameToUUID[live] ?: throw IllegalStateException()

            val buf = PacketByteBufs.create()
            buf.writeUUID(uuid)
            buf.writeBoolean(false)
            packets.add(buf)
            uuids.add(uuid)
            manager.live.remove(uuid)
        }

        return Pair(packets, uuids)
    }

    fun updateForPlayer(player: ServerPlayer, stuff: Pair<List<FriendlyByteBuf>, List<UUID>>) {
        updateForPlayer(player, stuff.first, stuff.second)
    }

    fun updateForPlayer(player: ServerPlayer, packets: List<FriendlyByteBuf>, uuids: List<UUID>) {
        for (buf in packets) {
            ServerPlayNetworking.send(player, ValleyUtils.id("set_live_status"), buf)
        }

        for (uuid in uuids) {
            val other = player.server.playerList.getPlayer(uuid) ?: continue
            player.connection.send(ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, other))
        }
    }

    abstract fun buildPackets(): Pair<List<FriendlyByteBuf>, List<UUID>>
}