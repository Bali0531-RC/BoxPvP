package org.bali.boxpvp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BoxPvp implements Listener {

    private final JavaPlugin plugin;
    private final Map<Location, Integer> placedBlocks; // Map to store placed blocks with their IDs
    private List<Material> despawnBlocks;
    private int despawnTime;
    private boolean debugEnabled;

    public BoxPvp(JavaPlugin plugin) {
        this.plugin = plugin;
        this.debugEnabled = plugin.getConfig().getBoolean("debug", false);
        this.placedBlocks = new HashMap<>();
        loadSettings();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void reloadSettings() {
        loadSettings();
    }

    private void loadSettings() {
        despawnBlocks = new ArrayList<>();
        File configFile = new File(plugin.getDataFolder(), "settings.yml");
        if (!configFile.exists()) {
            plugin.saveResource("settings.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        List<String> despawnBlocksString = config.getStringList("despawnBlocks");

        if (debugEnabled) {
            Bukkit.getLogger().log(Level.INFO, "Despawn blocks loaded from config: " + despawnBlocksString);
        }

        for (String materialName : despawnBlocksString) {
            Material material = Material.matchMaterial(materialName);
            if (material != null) {
                despawnBlocks.add(material);
            } else {
                plugin.getLogger().warning("Invalid material name in despawnBlocks: " + materialName);
            }
        }

        if (debugEnabled) {
            Bukkit.getLogger().log(Level.INFO, "Despawn blocks loaded: " + despawnBlocks);
        }

        // Default despawn time: 15 seconds
        despawnTime = config.getInt("despawnTime", 15);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!Settings.getInstance().isBoxPvpEnabled()) {
            return;
        }

        Material blockType = event.getBlock().getType();
        Location blockLocation = event.getBlock().getLocation();

        if (debugEnabled) {
            Bukkit.getLogger().log(Level.INFO, "Block placed: " + blockType.toString());
        }

        if (despawnBlocks.contains(blockType)) {
            int blockId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Material currentBlockType = blockLocation.getBlock().getType();
                if (currentBlockType.equals(blockType)) {
                    if (debugEnabled) {
                        Bukkit.getLogger().log(Level.INFO, "Despawning block: " + blockType.toString());
                    }
                    blockLocation.getBlock().setType(Material.AIR);
                    placedBlocks.remove(blockLocation); // Remove from map when despawned
                }
            }, 20L * despawnTime); // 20 ticks per second * despawnTime seconds

            placedBlocks.put(blockLocation, blockId); // Add block to map with its ID
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() == plugin) {
            despawnAllBlocks();
        }
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin() == plugin) {
            reloadSettings();
        }
    }

    private void despawnAllBlocks() {
        for (Location loc : placedBlocks.keySet()) {
            loc.getBlock().setType(Material.AIR);
        }
        placedBlocks.clear();
    }
}
