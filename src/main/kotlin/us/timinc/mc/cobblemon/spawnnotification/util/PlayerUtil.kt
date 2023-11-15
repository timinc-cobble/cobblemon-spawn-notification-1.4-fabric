package us.timinc.mc.cobblemon.spawnnotification.util

import us.timinc.mc.cobblemon.spawnnotification.config.SpawnNotificationConfig
import com.cobblemon.mod.common.util.server
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.dimension.DimensionType
import kotlin.math.sqrt

object PlayerUtil {
    private lateinit var config: SpawnNotificationConfig

    fun getPlayersInRange(pos: BlockPos, range: Int, dimensionKey: RegistryKey<DimensionType>): List<ServerPlayerEntity> {
        val serverInstance = server() ?: return emptyList()
        val allPlayers = serverInstance.playerManager.playerList

        var playersInRange = allPlayers.filter { sqrt(pos.getSquaredDistance(it.pos)) <= range && dimensionKey == it.world.dimensionKey}
        playersInRange = playersInRange.sortedBy { sqrt(pos.getSquaredDistance(it.pos)) }

        if (config.playerLimit > 0) {
            playersInRange = playersInRange.take(config.playerLimit)
        }

        return playersInRange
    }
}