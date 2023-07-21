package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.playSoundServer
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource

object SpawnNotification : ModInitializer {
    const val MOD_ID = "spawn_notification"
    private var config: SpawnNotificationConfig? = null
    var SHINY_SOUND_ID: ResourceLocation = ResourceLocation("spawnnotification:pla_shiny")
    var SHINY_SOUND_EVENT: SoundEvent = SoundEvent(SHINY_SOUND_ID)

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

        Registry.register(Registry.SOUND_EVENT, SHINY_SOUND_ID, SHINY_SOUND_EVENT)

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

        if (cachedConfig.playShinySound && pokemon.shiny) {
            level.playSoundServer(pokemonEntity.eyePosition, SHINY_SOUND_EVENT, SoundSource.NEUTRAL, 10f, 1f)
        }

        var messageComponent = Component.translatable(message, pokemonName)
        if (cachedConfig.broadcastCoords) {
            messageComponent = messageComponent.append(Component.translatable("spawnnotification.notification.coords", pos.x, pos.y, pos.z))
        }

        level.players().forEach { player ->
            player.sendSystemMessage(messageComponent)
        }
    }

}