package us.timinc.mc.cobblemon.spawnnotification.config

import com.google.gson.GsonBuilder
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import java.io.File
import java.io.FileReader
import java.io.PrintWriter

class SpawnNotificationConfig {
//    @Comment("Whether or not to send a chat message for legendary spawns")
    val broadcastLegendary = true
//    @Comment("Whether or not to send a chat message for shiny spawns")
    val broadcastShiny = true
//    @Comment("Whether or not to broadcast the coords")
    val broadcastCoords = true
//    @Comment("Whether or not to broadcast the biome")
    val broadcastBiome = false
//    @Comment("Whether or not to play the PLA shiny sound when a shiny spawns")
    val playShinySound = true
//    @Comment("Whether or not to play the PLA shiny sound when a player sends out a shiny")
    val playShinySoundPlayer = false
    val announceCrossDimensions = false
    val broadcastDespawns = false

    val broadcastRange: Int = 16

    // TODO: implement player limit
    // Sort allPlayers by distance then only do up to X times
    // Set to -1 for no limit
//    val playerLimit: Int = -1

    class Builder {
        companion object {
            fun load() : SpawnNotificationConfig {
                val gson = GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create()

                var config = SpawnNotificationConfig()
                val configFile = File("config/${SpawnNotification.MOD_ID}.json")
                configFile.parentFile.mkdirs()

                if (configFile.exists()) {
                    try {
                        val fileReader = FileReader(configFile)
                        config = gson.fromJson(fileReader, SpawnNotificationConfig::class.java)
                        fileReader.close()
                    } catch (e: Exception) {
                        println("Error reading config file")
                    }
                }

                val pw = PrintWriter(configFile)
                gson.toJson(config, pw)
                pw.close()

                return config
            }
        }
    }
}