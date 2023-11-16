package us.timinc.mc.cobblemon.spawnnotification.util

import com.cobblemon.mod.common.util.server
import net.minecraft.registry.RegistryKey
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.dimension.DimensionType
import kotlin.math.sqrt

object PlayerUtil {
    /**
     * Get a list of valid players.
     *
     * @param pos The center to block to search from.
     * @param range The distance (in block radius) to search out to.
     * @param dimensionKey The dimension to search in.
     * @param playerLimit The maximum number of players to accept (prioritized by least distance).
     *
     * @return The filtered list of players.
     */
    fun getValidPlayers(
        pos: BlockPos, range: Int, dimensionKey: RegistryKey<DimensionType>, playerLimit: Int
    ): List<ServerPlayerEntity> {
        return getValidPlayers(pos, range, dimensionKey).sortedBy { sqrt(pos.getSquaredDistance(it.pos)) }
            .take(playerLimit)
    }

    /**
     * Get a list of valid players.
     *
     * @param pos The center to block to search from.
     * @param range The distance (in block radius) to search out to.
     * @param dimensionKey The dimension to search in.
     *
     * @return The filtered list of players.
     */
    fun getValidPlayers(
        pos: BlockPos, range: Int, dimensionKey: RegistryKey<DimensionType>
    ): List<ServerPlayerEntity> {
        val serverInstance = server() ?: return emptyList()

        return serverInstance.playerManager.playerList.filter { sqrt(pos.getSquaredDistance(it.pos)) <= range && dimensionKey == it.world.dimensionKey }
    }
}