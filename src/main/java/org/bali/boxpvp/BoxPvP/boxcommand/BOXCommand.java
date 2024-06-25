package org.bali.boxpvp.BoxPvP.boxcommand;

import org.bali.boxpvp.BoxPvP.BoxPvp;
import org.bali.boxpvp.Main;
import org.bali.boxpvp.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class BOXCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final BoxPvp boxPvp;

    public BOXCommand(JavaPlugin plugin, BoxPvp boxPvp) {
        this.plugin = plugin;
        this.boxPvp = boxPvp;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("{author}", "bali0531"); // Replace with dynamic retrieval if needed
            replacements.put("{version}", plugin.getDescription().getVersion());

            MessageUtils.sendMsgP(sender, "Basic", replacements);
            sender.sendMessage(ChatColor.GREEN + "BoxPvP plugin v" + plugin.getDescription().getVersion() + " by: bali0531. To reload: /boxpvp reload");
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("createarena")) {
            if (!(sender instanceof Player) || sender.hasPermission("boxpvp.createarena")) {
                String startCoords = args[1];
                String endCoords = args[2];
                String worldName = ((Player) sender).getWorld().getName();

                List<String> arenas = Main.CONFIG.getStringList("arenas");
                arenas.add(startCoords + ";" + endCoords + ";" + worldName);
                Main.CONFIG.set("arenas", arenas);
                Main.getAbstractConfig().saveConfig(); // Save changes to config.yml

                boxPvp.reloadSettings(); // Reload arenas data
                MessageUtils.sendMsgP(sender, "arena_created");
                return true;
            } else {
                MessageUtils.sendMsgP(sender, "no_permission");
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!(sender instanceof Player) || sender.hasPermission("boxpvp.reload")) {
                Main.getAbstractConfig().reloadConfig();
                Main.getAbstractMessages().reloadConfig();

                boxPvp.reloadSettings(); // Reload despawn blocks data
                MessageUtils.sendMsgP(sender, "reload_success");
                return true;
            } else {
                MessageUtils.sendMsgP(sender, "no_permission");
                return true;
            }
        } else {
            MessageUtils.sendMsgP(sender, "usage");
            return true;
        }
    }
}
