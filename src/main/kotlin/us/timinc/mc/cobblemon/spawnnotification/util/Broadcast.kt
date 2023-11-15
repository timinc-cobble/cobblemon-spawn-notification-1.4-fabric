package us.timinc.mc.cobblemon.spawnnotification.util

import com.cobblemon.mod.common.util.server
import net.minecraft.server.world.ServerWorld
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object Broadcast {
    fun broadcastMessage(message: Text) {
        val serverInstance = server()?:return
        serverInstance.sendMessage(message)
        serverInstance.playerManager.playerList.forEach { it.sendMessage(message) }
    }

    fun broadcastMessage(level: ServerWorld, message: Text) {
        level.players.forEach { it.sendMessage(message) }
    }

    fun broadcastMessage(players: List<ServerPlayerEntity>, message: Text) {
        players.forEach { it.sendMessage(message) }
    }

    fun broadcastMessage(player: ServerPlayerEntity, message: Text) {
        player.sendMessage(message)
    }
}