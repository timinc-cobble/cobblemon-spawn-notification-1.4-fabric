package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.entity.SpawnEvent
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.playSoundServer
import com.cobblemon.mod.common.util.server
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.entity.passive.HorseEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
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

            broadcastSpawn(evt)
            if (config.playShinySound && pokemon.shiny) {
                playShinySound(evt.ctx.world, evt.ctx.position)
            }
        }
        CobblemonEvents.POKEMON_SENT_POST.subscribe { evt ->
            if (config.playShinySoundPlayer && evt.pokemon.shiny) {
                playShinySound(evt.pokemonEntity.world, evt.pokemonEntity.blockPos)
            }
        }
        CobblemonEvents.POKEMON_CAPTURED.subscribe { evt ->
            broadcastDespawn(evt.pokemon, DespawnReason.CAPTURED)
        }
        CobblemonEvents.POKEMON_FAINTED.subscribe { evt ->
            broadcastDespawn(evt.pokemon, DespawnReason.FAINTED)
        }
        ServerEntityEvents.ENTITY_UNLOAD.register{ entity, _ ->
            if (entity !is PokemonEntity) return@register

            broadcastDespawn(entity.pokemon, DespawnReason.DESPAWNED)
        }
    }

    private fun broadcastDespawn(
        pokemon: Pokemon,
        reason: DespawnReason
    ) {
        if (config.broadcastDespawns && (pokemon.shiny || pokemon.isLegendary())) {
            server()?.playerManager
                ?.playerList
                ?.forEach { player ->
                    player.sendMessage(
                        Text.translatable(
                            "$MOD_ID.notification.${reason.translationKey}",
                            pokemon.getDisplayName()
                        )
                    )
                }
        }
    }

    private fun broadcastSpawn(
        evt: SpawnEvent<PokemonEntity>
    ) {
        val pokemon = evt.entity.pokemon
        val pokemonName = pokemon.getDisplayName()

        val message = when {
            config.broadcastLegendary && config.broadcastShiny && pokemon.isLegendary() && pokemon.shiny -> "$MOD_ID.notification.both"
            config.broadcastLegendary && pokemon.isLegendary() -> "$MOD_ID.notification.legendary"
            config.broadcastShiny && pokemon.shiny -> "$MOD_ID.notification.shiny"
            else -> return
        }

        var messageComponent = Text.translatable(message, pokemonName)
        val pos = evt.ctx.position
        if (config.broadcastCoords) {
            messageComponent = messageComponent.append(
                Text.translatable(
                    "$MOD_ID.notification.coords",
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
                    "$MOD_ID.notification.biome",
                    Text.translatable("biome.${evt.ctx.biomeName.toTranslationKey()}")
                )
            )
        }

        if (config.announceCrossDimensions) {
            messageComponent = messageComponent.append(Text.translatable(
                "$MOD_ID.notification.dimension",
                Text.translatable("dimension.${level.dimensionKey.value.toTranslationKey()}")
            ))

            server()?.playerManager
                ?.playerList
                ?.forEach { player ->
                    player.sendMessage(messageComponent)
                }
        } else {
            level.players.forEach { player ->
                player.sendMessage(messageComponent)
            }
        }
    }

    private fun playShinySound(
        level: World,
        pos: BlockPos
    ) {
        level.playSoundServer(pos.toCenterPos(), SHINY_SOUND_EVENT, SoundCategory.NEUTRAL, 10f, 1f)
    }
}