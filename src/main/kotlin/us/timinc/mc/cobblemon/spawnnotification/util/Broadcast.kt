package us.timinc.mc.cobblemon.spawnnotification.util

import com.cobblemon.mod.common.util.server
import net.minecraft.server.world.ServerWorld
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
}