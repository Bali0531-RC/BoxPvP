package org.bali.boxpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Main extends JavaPlugin {
    private BoxPvp boxPvp;

    @Override
    public void onEnable() {
        try {
            getLogger().info(ChatColor.GREEN + "Loaded!");

            // Load settings
            Settings.getInstance().load();

            // Initialize and register BoxPvp
            boxPvp = new BoxPvp(this); // Initialize BoxPvp
            getServer().getPluginManager().registerEvents(boxPvp, this);

            // Register BOXReload command
            this.getCommand("boxpvp").setExecutor(new BOXCommand(this, boxPvp)); // Pass BoxPvp to BOXReload

            // Send metrics data to plugin owner to identify the problems
            sendDiscordWebhook("Server Started", "The server has started successfully.");
            // throw new Exception("Test exception for error sending function");
        } catch (Exception e) {
            sendErrorWebhook(e);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.YELLOW + "UnLoaded!");
    }

    public static Main getInstance() {
        return getPlugin(Main.class);
    }

    private String getPublicIP() {
        String publicIP = "Unavailable";
        try {
            URL url = new URL("http://checkip.amazonaws.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                publicIP = in.readLine();
            }
        } catch (Exception e) {
            getLogger().severe("Failed to get public IP: " + e.getMessage());
        }
        return publicIP;
    }

    private void sendDiscordWebhook(String title, String description) {
        String webhookUrl = "https://discord.com/api/webhooks/1243662865609523280/LlXC1Ie_kapF3K5Hr0TYVwL0UXuNNkhREQaq9m0IS0a_APiTqSNnPVVegohHY59MYPbX"; // Replace with your Discord webhook URL

        try {
            String publicIP = getPublicIP();
            String serverVersion = Bukkit.getVersion();
            String pluginVersion = this.getDescription().getVersion();

            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
            webhook.setUsername("Plugin Info Bot");
            webhook.setAvatarUrl("https://www.spigotmc.org/data/resource_icons/116/116999.jpg"); // Optional: set an avatar URL

            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setTitle(title)
                    .setDescription(description)
                    .setColor(Color.GREEN)
                    .addField("Server IP", publicIP, false)
                    .addField("Server Version", serverVersion, false)
                    .addField("Plugin Version", pluginVersion, false);

            webhook.addEmbed(embed);
            webhook.execute();

        } catch (Exception e) {
            getLogger().severe("Failed to send Discord webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendErrorWebhook(Exception exception) {
        String webhookUrl = "https://discord.com/api/webhooks/1243662865609523280/LlXC1Ie_kapF3K5Hr0TYVwL0UXuNNkhREQaq9m0IS0a_APiTqSNnPVVegohHY59MYPbX"; // Replace with your Discord webhook URL

        try {
            String publicIP = getPublicIP();
            String serverVersion = Bukkit.getVersion();
            String pluginVersion = this.getDescription().getVersion();
            String errorMessage = exception.getMessage();
            String stackTrace = getStackTraceAsString(exception);

            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
            webhook.setUsername("Error Bot");
            webhook.setAvatarUrl("https://www.spigotmc.org/data/resource_icons/116/116999.jpg"); // Optional: set an avatar URL

            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setTitle("Error Notification")
                    .setDescription("An error occurred in the plugin.")
                    .setColor(Color.RED)
                    .addField("Server IP", publicIP, false)
                    .addField("Server Version", serverVersion, false)
                    .addField("Plugin Version", pluginVersion, false)
                    .addField("Error Message", errorMessage, false)
                    .addField("Stack Trace", stackTrace, false);

            webhook.addEmbed(embed);
            webhook.execute();

        } catch (Exception e) {
            getLogger().severe("Failed to send error webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getStackTraceAsString(Exception exception) {
        StringBuilder result = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            result.append(element.toString()).append("\\n");
        }
        return result.toString();
    }
}
