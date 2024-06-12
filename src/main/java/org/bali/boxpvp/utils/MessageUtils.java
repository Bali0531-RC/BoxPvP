package org.bali.boxpvp.utils;

import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


import static org.bali.boxpvp.Main.CONFIG;
import static org.bali.boxpvp.Main.MESSAGES;


public class MessageUtils {

    public static void sendMsgP(CommandSender p, String path) {
        String message = MESSAGES.getString(path);
        if (message == null || message.isEmpty()) return; // Check if the message is null or empty
        p.sendMessage(ColorUtils.format(CONFIG.getString("prefix") + message));
    }
    public static void sendTitle(Player player, String path) {
        String title = MESSAGES.getString(path + ".title");
        String subtitle = MESSAGES.getString(path + ".subtitle");
        int fadeIn = MESSAGES.getInt(path + ".fadeIn", 10);
        int stay = MESSAGES.getInt(path + ".stay", 70);
        int fadeOut = MESSAGES.getInt(path + ".fadeOut", 20);

        if (title == null || title.isEmpty()) return;
        player.sendTitle(ColorUtils.format(title), ColorUtils.format(subtitle), fadeIn, stay, fadeOut);
    }


    public static void sendMsgP(Player p, String path) {
        String message = MESSAGES.getString(path);
        if (message == null || message.isEmpty()) return; // Check if the message is null or empty
        p.sendMessage(ColorUtils.format(CONFIG.getString("prefix") + message));
    }

    public static void sendMsgP(Player p, String path, Map<String, String> replacements) {
        String messageString = MESSAGES.getString(path);
        if (messageString == null) {
            // Log an error or handle the null value
            return;
        }
        AtomicReference<String> message = new AtomicReference<>(messageString);
        replacements.forEach((key, value) -> message.set(message.get().replace(key, value)));
        p.sendMessage(ColorUtils.format(CONFIG.getString("prefix") + message.get()));
    }

    public static void sendMsgP(CommandSender p, String path, Map<String, String> replacements) {
        String messageString = MESSAGES.getString(path);
        if (messageString == null) {
            // Log an error or handle the null value
            return;
        }
        AtomicReference<String> message = new AtomicReference<>(messageString);
        replacements.forEach((key, value) -> message.set(message.get().replace(key, value)));
        p.sendMessage(ColorUtils.format(CONFIG.getString("prefix") + message.get()));
    }

    public static void sendMsg(Player p, String path) {
        String message = MESSAGES.getString(path);
        if (message == null) return; // Check if the message is null
        p.sendMessage(ColorUtils.format(message));
    }

    public static void sendListMsg(Player p, String path) {
        for (String m : MESSAGES.getStringList(path)) {
            p.sendMessage(ColorUtils.format(m));
        }
    }

    public static void sendListMsg(CommandSender p, String path) {
        for (String m : MESSAGES.getStringList(path)) {
            p.sendMessage(ColorUtils.format(m));
        }
    }


}
