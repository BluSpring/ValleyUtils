package xyz.bluspring.valleyutils.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import xyz.bluspring.valleyutils.ValleyUtils
import xyz.bluspring.valleyutils.live.ClientLiveManager

class ValleyUtilsClient : ClientModInitializer {

    override fun onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            ClientPlayNetworking.send(ValleyUtils.HAS_VALLEY_UTILS_PACKET, PacketByteBufs.empty())
        }

        liveManager.live
    }

    companion object {
        val liveManager = ClientLiveManager()
    }
}
