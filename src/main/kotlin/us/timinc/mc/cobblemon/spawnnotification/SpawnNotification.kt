package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel


object SpawnNotification : ModInitializer {
    const val MOD_ID = "spawn_notification"
    private var config: SpawnNotificationConfig? = null

    override fun onInitialize() {
        AutoConfig.register(SpawnNotificationConfig::class.java
        ) { definition: Config?, configClass: Class<SpawnNotificationConfig?>? ->
            JanksonConfigSerializer(
                definition,
                configClass
            )
        }
        config = AutoConfig.getConfigHolder(SpawnNotificationConfig::class.java)
            .config

        ServerEntityEvents.ENTITY_LOAD.register { entity, world ->
            if (entity !is PokemonEntity || entity.pokemon.isPlayerOwned()) {
                return@register
            }

            possiblyBroadcastSpawn(entity, world)
        }
    }

    private fun possiblyBroadcastSpawn(pokemonEntity: PokemonEntity, level: ServerLevel) {
        val cachedConfig = config ?: return

        val pokemon = pokemonEntity.pokemon
        if (pokemon.isPlayerOwned()) return

        val pokemonName = pokemon.displayName
        val pos = pokemonEntity.blockPosition()

        val message = when {
            cachedConfig.broadcastLegendary && cachedConfig.broadcastShiny && pokemon.isLegendary() && pokemon.shiny -> "spawnnotification.notification.both"
            cachedConfig.broadcastLegendary && pokemon.isLegendary() -> "spawnnotification.notification.legendary"
            cachedConfig.broadcastShiny && pokemon.shiny -> "spawnnotification.notification.shiny"
            else -> return
        }

        level.players().forEach { player ->
            player.sendSystemMessage(Component.translatable(message, pokemonName))
            if (cachedConfig.broadcastCoords) {
                player.sendSystemMessage(Component.literal("${pos.x}, ${pos.y}, ${pos.z}s"))
            }
        }

    }

}