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

        // Send metrics data to plugin owner to identify the problems 
        sendDiscordWebhook();
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.YELLOW + "UnLoaded!");
    }

    public static Main getInstance() {
        return getPlugin(Main.class);
    }
        private void sendDiscordWebhook() {
        String webhookUrl = "YOUR_DISCORD_WEBHOOK_URL"; // Replace with your Discord webhook URL

        try {
            String serverIP = InetAddress.getLocalHost().getHostAddress();
            String serverVersion = Bukkit.getVersion();
            String pluginVersion = this.getDescription().getVersion();

            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
            webhook.setUsername("Server Info Bot");
            webhook.setAvatarUrl("https://example.com/avatar.png"); // Optional: set an avatar URL

            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setTitle("Server Started")
                    .setColor(Color.GREEN)
                    .addField("Server IP", serverIP, false)
                    .addField("Server Version", serverVersion, false)
                    .addField("Plugin Version", pluginVersion, false);

            webhook.addEmbed(embed);
            webhook.execute();

            getLogger().info("Discord webhook sent successfully!");
        } catch (Exception e) {
            getLogger().severe("Failed to send Discord webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
