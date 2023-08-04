package us.timinc.mc.cobblemon.spawnnotification.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification

@Config(name = SpawnNotification.MOD_ID)
class SpawnNotificationConfig : ConfigData {
    @Comment("Whether or not to send a chat message for legendary spawns")
    val broadcastLegendary = true
    @Comment("Whether or not to send a chat message for shiny spawns")
    val broadcastShiny = true
    @Comment("Whether or not to broadcast the coords")
    val broadcastCoords = true
    @Comment("Whether or not to broadcast the biome")
    val broadcastBiome = false
    @Comment("Whether or not to play the PLA shiny sound when a shiny spawns")
    val playShinySound = true
    @Comment("Whether or not to play the PLA shiny sound when a player sends out a shiny")
    val playShinySoundPlayer = false
}