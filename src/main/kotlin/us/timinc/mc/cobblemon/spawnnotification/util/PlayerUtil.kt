package us.timinc.mc.cobblemon.spawnnotification.util

import com.cobblemon.mod.common.util.server
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import kotlin.math.sqrt

object PlayerUtil {
    fun getPlayersInRange(pos: BlockPos, range: Int): List<ServerPlayerEntity> {
        val serverInstance = server() ?: return emptyList()
        val allPlayers = serverInstance.playerManager.playerList

        return allPlayers.filter { sqrt(pos.getSquaredDistance(it.pos)) <= range }//.sortedBy { sqrt(pos.getSquaredDistance(it.pos)) }
    }
}