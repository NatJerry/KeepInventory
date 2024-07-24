package org.ncc.keepInventory

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedOutputStream
import java.io.File
import java.net.http.WebSocket.Listener
import java.nio.charset.StandardCharsets

class Main : JavaPlugin(), Listener {
    val json: Gson = Gson()

    data class WorldSetting(
        var worldName: String,
        var isKeepInventory: Boolean,
        var isKeepExp: Boolean
    )

    val worldSettingList: MutableList<WorldSetting> = mutableListOf()


    override fun onEnable() {
        Bukkit.getWorlds().forEach { world ->
            run {
                val file = File(dataFolder, "${world.name}.json")
                var out = BufferedOutputStream(file.outputStream())
                var worldSetting = WorldSetting(world.name, true, true)
                if (!file.exists()) {
                    if (!file.parentFile.exists()) file.parentFile.mkdirs()
                    out.use {
                        it.write(json.toJson(worldSetting).toByteArray(StandardCharsets.UTF_8))
                    }
                }
                worldSettingList.add(json.fromJson(file.reader(StandardCharsets.UTF_8), WorldSetting::class.java))
            }
        }
        logger.info("worldSettingList loaded")

    }

    override fun onDisable() {

    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {

    }

}
