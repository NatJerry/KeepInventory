package org.ncc.keepInventory

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedOutputStream
import java.io.File
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
                val worldSetting = WorldSetting(world.name, true, true)
                if (!file.exists()) {
                    if (!file.parentFile.exists()) file.parentFile.mkdirs()
                    file.createNewFile()
                    val out = BufferedOutputStream(file.outputStream())
                    out.use {
                        it.write(json.toJson(worldSetting).toByteArray(StandardCharsets.UTF_8))
                    }
                }
                worldSettingList.add(json.fromJson(file.reader(StandardCharsets.UTF_8), WorldSetting::class.java))
            }
        }
        logger.info("worldSettingList loaded")
        Bukkit.getPluginManager().registerEvents(this,this)
        logger.info("Event Listener Registered")
        logger.info("Enabled,to change WorldSetting,go to modify the json file of Each World and then restart the plugin")

    }

    override fun onDisable() {

    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val playerWorldName = event.player.world.name
        worldSettingList.forEach { worldSetting ->
            run {
                if(worldSetting.worldName.equals(playerWorldName)){
                    if(worldSetting.isKeepExp){
                        event.keepLevel = true
                        event.droppedExp = 0
                    }
                    if(worldSetting.isKeepInventory){
                        event.keepInventory = true
                        event.drops.clear()
                    }
                    return
                }
            }
        }
    }

}
