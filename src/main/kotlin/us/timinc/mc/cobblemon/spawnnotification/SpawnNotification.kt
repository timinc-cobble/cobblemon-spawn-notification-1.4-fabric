package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.entity.SpawnEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.playSoundServer
import net.fabricmc.api.ModInitializer
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import us.timinc.mc.cobblemon.spawnnotification.config.SpawnNotificationConfig

object SpawnNotification : ModInitializer {
    const val MOD_ID = "spawn_notification"
    private lateinit var config: SpawnNotificationConfig

    @JvmStatic
    var SHINY_SOUND_ID: Identifier = Identifier("$MOD_ID:pla_shiny")

    @JvmStatic
    var SHINY_SOUND_EVENT: SoundEvent = SoundEvent.of(SHINY_SOUND_ID)

    override fun onInitialize() {
        config = SpawnNotificationConfig.Builder.load()

        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe { evt ->
            val pokemon = evt.entity.pokemon
            if (pokemon.isPlayerOwned()) return@subscribe

            broadcastSpawn(config, evt)
            playShinySound(config, evt.entity.pokemon, evt.ctx.world, evt.ctx.position)
        }
        CobblemonEvents.POKEMON_SENT_POST.subscribe { evt ->
            val pokemon = evt.pokemonEntity
            playShinySound(config, evt.pokemon, evt.pokemonEntity.world, evt.pokemonEntity.blockPos)
        }
    }

    private fun broadcastSpawn(
        config: SpawnNotificationConfig,
        evt: SpawnEvent<PokemonEntity>
    ) {
        val pokemon = evt.entity.pokemon
        val pokemonName = pokemon.getDisplayName()

        val message = when {
            config.broadcastLegendary && config.broadcastShiny && pokemon.isLegendary() && pokemon.shiny -> "spawnnotification.notification.both"
            config.broadcastLegendary && pokemon.isLegendary() -> "spawnnotification.notification.legendary"
            config.broadcastShiny && pokemon.shiny -> "spawnnotification.notification.shiny"
            else -> return
        }

        var messageComponent = Text.translatable(message, pokemonName)
        val pos = evt.ctx.position
        if (config.broadcastCoords) {
            messageComponent = messageComponent.append(
                Text.translatable(
                    "spawnnotification.notification.coords",
                    pos.x,
                    pos.y,
                    pos.z
                )
            )
        }
        val level = evt.ctx.world
        if (config.broadcastBiome) {
            messageComponent = messageComponent.append(
                Text.translatable(
                    "spawnnotification.notification.biome",
                    Text.translatable("biome.${evt.ctx.biomeName.toTranslationKey()}")
                )
            )
        }

        level.players.forEach { player ->
            player.sendMessage(messageComponent)
        }
    }

    private fun playShinySound(
        cachedConfig: SpawnNotificationConfig,
        pokemon: Pokemon,
        level: World,
        pos: BlockPos
    ) {
        if (cachedConfig.playShinySound && pokemon.shiny) {
            level.playSoundServer(pos.toCenterPos(), SHINY_SOUND_EVENT, SoundCategory.NEUTRAL, 10f, 1f)
        }
    }
}