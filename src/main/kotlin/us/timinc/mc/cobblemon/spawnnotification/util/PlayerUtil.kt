package us.timinc.mc.cobblemon.spawnnotification.util

import com.cobblemon.mod.common.util.server
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.dimension.DimensionType
import kotlin.math.sqrt

object PlayerUtil {
    fun getPlayersInRange(pos: BlockPos, range: Int, dimensionKey: RegistryKey<DimensionType>): List<ServerPlayerEntity> {
        val serverInstance = server() ?: return emptyList()
        val allPlayers = serverInstance.playerManager.playerList

        return allPlayers.filter { sqrt(pos.getSquaredDistance(it.pos)) <= range && dimensionKey == it.world.dimensionKey}//.sortedBy { sqrt(pos.getSquaredDistance(it.pos)) }
    }
}