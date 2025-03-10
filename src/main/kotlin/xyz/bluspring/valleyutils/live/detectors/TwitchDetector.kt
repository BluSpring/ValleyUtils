package xyz.bluspring.valleyutils.live.detectors

import net.minecraft.network.FriendlyByteBuf
import xyz.bluspring.valleyutils.live.ServerLiveManager
import xyz.bluspring.valleyutils.live.util.TwitchApi
import java.net.URI
import java.util.*

class TwitchDetector(manager: ServerLiveManager) : LiveDetector(manager) {
    var lastScan = 0L

    init {
        val properties = Properties()
        properties.load(this::class.java.getResourceAsStream("/twitch_to_mc.properties"))

        for ((username, uuid) in properties) {
            usernameToUUID[username as String] = UUID.fromString(uuid as String)
        }
    }

    override fun buildPackets(): Pair<List<FriendlyByteBuf>, List<UUID>> {
        return buildPackets(isLive, listOf())
    }

    override fun manualScan() {
        val data = TwitchApi.get(URI.create("https://api.twitch.tv/helix/streams?user_login=${usernameToUUID.keys.joinToString("&user_login=")}")) ?: return

        val isStreaming = mutableSetOf<String>()

        val streams = data.getAsJsonArray("data")
        for (stream in streams) {
            val obj = stream.asJsonObject
            val login = obj.get("user_login").asString

            isStreaming.add(login)
        }

        val notStreaming = isLive.toMutableSet()
        notStreaming.removeAll(isStreaming)

        update(isStreaming, notStreaming)
        lastScan = System.currentTimeMillis()
    }
}