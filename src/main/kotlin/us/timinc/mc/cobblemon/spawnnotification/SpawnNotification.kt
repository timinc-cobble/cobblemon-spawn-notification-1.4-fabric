package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.playSoundServer
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3

object SpawnNotification : ModInitializer {
    const val MOD_ID = "spawn_notification"
    private var config: SpawnNotificationConfig? = null

    @JvmStatic
    var SHINY_SOUND_ID: ResourceLocation = ResourceLocation("spawnnotification:pla_shiny")

    @JvmStatic
    var SHINY_SOUND_EVENT: SoundEvent = SoundEvent(SHINY_SOUND_ID)

    override fun onInitialize() {
        AutoConfig.register(
            SpawnNotificationConfig::class.java
        ) { definition: Config?, configClass: Class<SpawnNotificationConfig?>? ->
            JanksonConfigSerializer(
                definition,
                configClass
            )
        }
        config = AutoConfig.getConfigHolder(SpawnNotificationConfig::class.java)
            .config

        Registry.register(Registry.SOUND_EVENT, SHINY_SOUND_ID, SHINY_SOUND_EVENT)
    }

    fun possiblyBroadcastSpawn(pokemonEntity: PokemonEntity, level: ServerLevel, blockPos: BlockPos) {
        val cachedConfig = config ?: return

        val pokemon = pokemonEntity.pokemon
        if (pokemon.isPlayerOwned()) return

        broadcastSpawn(cachedConfig, pokemonEntity, level, blockPos)
        playShinySound(cachedConfig, pokemonEntity, level, blockPos)
    }

    private fun broadcastSpawn(
        config: SpawnNotificationConfig,
        pokemonEntity: PokemonEntity,
        level: ServerLevel,
        pos: BlockPos
    ) {
        val pokemon = pokemonEntity.pokemon
        val pokemonName = pokemon.displayName

        val message = when {
            config.broadcastLegendary && config.broadcastShiny && pokemon.isLegendary() && pokemon.shiny -> "spawnnotification.notification.both"
            config.broadcastLegendary && pokemon.isLegendary() -> "spawnnotification.notification.legendary"
            config.broadcastShiny && pokemon.shiny -> "spawnnotification.notification.shiny"
            else -> return
        }

        var messageComponent = Component.translatable(message, pokemonName)
        if (config.broadcastCoords) {
            messageComponent = messageComponent.append(
                Component.translatable(
                    "spawnnotification.notification.coords",
                    pos.x,
                    pos.y,
                    pos.z
                )
            )
        }
        if (config.broadcastBiome) {
            val biomeHolder = level.getBiome(pos)
            val biomeBaseKey =
                "biome." + level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biomeHolder.value())
                    ?.toLanguageKey()
            messageComponent = messageComponent.append(
                Component.translatable(
                    "spawnnotification.notification.biome",
                    Component.translatable(biomeBaseKey)
                )
            )
        }

        level.players().forEach { player ->
            player.sendSystemMessage(messageComponent)
        }
    }

    fun possiblyPlayShinySound(
        pokemonEntity: PokemonEntity,
        level: ServerLevel,
        blockPos: BlockPos
    ) {
        val cachedConfig = config ?: return
        if (!pokemonEntity.pokemon.shiny) {
            return
        }
        playShinySound(cachedConfig, pokemonEntity, level, blockPos)
    }

    private fun playShinySound(
        cachedConfig: SpawnNotificationConfig,
        pokemonEntity: PokemonEntity,
        level: ServerLevel,
        blockPos: BlockPos
    ) {
        val pokemon = pokemonEntity.pokemon

        if (cachedConfig.playShinySound && pokemon.shiny) {
            level.playSoundServer(Vec3.atCenterOf(blockPos), SHINY_SOUND_EVENT, SoundSource.NEUTRAL, 10f, 1f)
        }
    }
}