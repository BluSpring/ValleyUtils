package xyz.bluspring.valleyutils.live.detectors

/*import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.network.FriendlyByteBuf
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import xyz.bluspring.valleyutils.live.ServerLiveManager
import java.net.URI
import java.util.*

class YoutubeDetector(manager: ServerLiveManager) : LiveDetector(manager) {
    init {
        val properties = Properties()
        properties.load(this::class.java.getResourceAsStream("/youtube_to_mc.properties"))

        for ((username, uuid) in properties) {
            usernameToUUID[username as String] = UUID.fromString(uuid as String)
        }
    }

    var socket: YoutubeSocket
    private var shouldClose = false

    init {
        this.socket = YoutubeSocket()
    }

    override fun buildPackets(): Pair<List<FriendlyByteBuf>, List<UUID>> {
        return buildPackets(isLive, listOf())
    }

    override fun manualScan() {
        if (!socket.isConnected)
            return

        for (channelId in usernameToUUID.keys) {
            socket.send("get_streams", JsonObject().apply {
                addProperty("id", channelId)
            })
        }
    }

    fun onClose() {
        shouldClose = true
        this.socket.close()
    }

    inner class YoutubeSocket : WebSocketClient(URI.create("wss://grievance-slimness-swerve.devos.gay")) {
        var isConnected = false

        fun send(op: String, data: JsonObject) {
            send(JsonObject().apply {
                this.addProperty("op", op)
                this.add("d", data)
            }.toString())
        }

        override fun onOpen(handshakedata: ServerHandshake?) {
            this@YoutubeDetector.socket = this
            isConnected = true
            send("login", JsonObject().apply {
                addProperty("password", "OhFUCKoff1")
            })
        }

        override fun onMessage(message: String) {
            val msg = JsonParser.parseString(message).asJsonObject
            val data = if (msg.has("d")) msg.getAsJsonObject("d") else JsonObject()

            when (msg.get("op").asString) {
                "login_ack" -> {
                    for (channelId in usernameToUUID.keys) {
                        send("get_streams", JsonObject().apply {
                            addProperty("id", channelId)
                        })
                    }
                }

                "streams_list" -> {
                    val channelId = data.get("id").asString
                    val streamIds = data.getAsJsonArray("streams").map { it.asString }

                    if (streamIds.isNotEmpty() && isLive.add(channelId)) {
                        update(listOf(channelId), listOf())
                    } else if (streamIds.isEmpty() && isLive.remove(channelId)) {
                        update(listOf(), listOf(channelId))
                    }
                }
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            if (!shouldClose)
                this@YoutubeDetector.socket = YoutubeSocket()
        }

        override fun onError(ex: Exception?) {
            ex?.printStackTrace()
        }

    }
}*/