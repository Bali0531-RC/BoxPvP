package org.bali.boxpvp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
            sender.sendMessage(ChatColor.GREEN + "BoxPvP plugin v" + plugin.getDescription().getVersion() + " by: bali0531. To reload: /boxpvp reload");
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!(sender instanceof Player) || sender.hasPermission("boxpvp.reload")) {
                Settings.getInstance().load();
                boxPvp.reloadSettings(); // Reload despawn blocks data
                sender.sendMessage(ChatColor.GREEN + "[BoxPVP] Configuration reloaded!");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /boxpvp reload");
            return true;
        }
    }
}