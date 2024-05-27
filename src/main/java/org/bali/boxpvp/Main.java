package org.bali.boxpvp;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private BoxPvp boxPvp;
    @Override
    public void onEnable() {
        getLogger().info(ChatColor.GREEN + "Loaded!");

        // Load settings
        Settings.getInstance().load();

        // Initialize and register BoxPvp
        boxPvp = new BoxPvp(this); // Initialize BoxPvp
        getServer().getPluginManager().registerEvents(boxPvp, this);

        // Register BOXReload command
        this.getCommand("boxpvp").setExecutor(new BOXCommand(this, boxPvp)); // Pass BoxPvp to BOXReload
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.YELLOW + "UnLoaded!");
    }

    public static Main getInstance() {
        return getPlugin(Main.class);
    }
}
