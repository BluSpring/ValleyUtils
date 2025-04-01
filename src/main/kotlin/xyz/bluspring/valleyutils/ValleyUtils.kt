package xyz.bluspring.valleyutils

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder
import net.kyrptonaught.customportalapi.portal.PortalIgnitionSource
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import xyz.bluspring.valleyutils.client.ValleyUtilsClient
import xyz.bluspring.valleyutils.live.LiveManager
import xyz.bluspring.valleyutils.live.ServerLiveManager
import xyz.bluspring.valleyutils.live.util.TwitchApi
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

        ServerTickEvents.END_SERVER_TICK.register { server ->
            for (player in server.playerList.players) {
                if (!player.tags.contains("lv_moved_dimensions_v2")) {
                    when (player.level().dimension()) {
                        Level.OVERWORLD -> {
                            player.teleportTo(server.getLevel(OLD_OVERWORLD), player.xo, player.yo, player.zo, player.yRot, player.xRot)
                        }

                        Level.NETHER -> {
                            player.teleportTo(server.getLevel(OLD_NETHER), player.xo, player.yo, player.zo, player.yRot, player.xRot)
                        }

                        Level.END -> {
                            player.teleportTo(server.getLevel(OLD_END), player.xo, player.yo, player.zo, player.yRot, player.xRot)
                        }
                    }

                    player.addTag("lv_moved_dimensions_v2")
                }
            }
        }

        if (TwitchApi.clientSecret.isNotBlank()) {
            ServerLifecycleEvents.SERVER_STARTING.register {
                liveManager = ServerLiveManager(it)
            }

            ServerLifecycleEvents.SERVER_STOPPING.register {
                liveManager?.onClose()
            }
        }

        CustomPortalBuilder.beginPortal()
            .frameBlock(Blocks.MOSS_BLOCK)
            .lightWithItem(Items.POTION)
            .destDimID(id("old_overworld"))
            .tintColor(120, 51, 174)
            .returnDim(Level.OVERWORLD.location(), false)
            .tintColor(36, 156, 229)
            .registerPortal()

        CustomPortalBuilder.beginPortal()
            .frameBlock(Blocks.CRYING_OBSIDIAN)
            .customIgnitionSource(PortalIgnitionSource.FIRE)
            .destDimID(id("old_nether"))
            .tintColor(120, 51, 174)
            .returnDim(Level.NETHER.location(), false)
            .registerPortal()
    }

    companion object {
        const val MOD_ID = "lavender_valley"
        val hasValleyUtilsPlayers = ConcurrentLinkedQueue<Player>()

        @JvmField val OLD_OVERWORLD = ResourceKey.create(Registries.DIMENSION, id("old_overworld"))
        @JvmField val OLD_NETHER = ResourceKey.create(Registries.DIMENSION, id("old_nether"))
        @JvmField val OLD_END = ResourceKey.create(Registries.DIMENSION, id("old_end"))

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
