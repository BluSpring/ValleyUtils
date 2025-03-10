package xyz.bluspring.valleyutils.live

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import xyz.bluspring.valleyutils.ValleyUtils

class ClientLiveManager : LiveManager() {
    init {
        ClientPlayNetworking.registerGlobalReceiver(ValleyUtils.id("set_live_status")) { mc, listener, buf, sender ->
            val uuid = buf.readUUID()
            val isLive = buf.readBoolean()

            if (isLive) {
                this.live.add(uuid)
            } else {
                this.live.remove(uuid)
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(ValleyUtils.id("get_current_live")) { mc, listener, _, sender ->
            val buf = PacketByteBufs.create()
            buf.writeVarInt(this.live.size)
            for (uuid in this.live) {
                buf.writeUUID(uuid)
            }

            mc.execute {
                ClientPlayNetworking.send(ValleyUtils.id("get_current_live"), buf)
            }
        }
        
        ClientPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            this.live.clear()
        }
    }
}