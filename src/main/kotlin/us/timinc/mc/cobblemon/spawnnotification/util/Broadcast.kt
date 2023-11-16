package us.timinc.mc.cobblemon.spawnnotification.util

import com.cobblemon.mod.common.util.server
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text

object Broadcast {
    /**
     * Broadcasts a message to all players on the server.
     *
     * @param message The message to send.
     */
    fun broadcastMessage(message: Text) {
        val serverInstance = server() ?: return
        serverInstance.sendMessage(message)
        broadcastMessage(serverInstance.playerManager.playerList, message)
    }

    /**
     * Broadcasts a message to all players in a given level.
     *
     * @param level The level to get the players from.
     * @param message The message to send.
     */
    fun broadcastMessage(level: ServerWorld, message: Text) {
        broadcastMessage(level.players, message)
    }

    /**
     * Broadcasts a message to all players in a given list.
     *
     * @param players The list of players.
     * @param message The message to send.
     */
    fun broadcastMessage(players: List<ServerPlayerEntity>, message: Text) {
        players.forEach { broadcastMessage(it, message) }
    }

    /**
     * Broadcasts a message to a particular player.
     *
     * @param player The player.
     * @param message The message to send.
     */
    private fun broadcastMessage(player: ServerPlayerEntity, message: Text) {
        player.sendMessage(message)
    }
}