package de.jonahd345.extendedeconomy.command;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Message;
import de.jonahd345.extendedeconomy.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtendedEconomyCommand implements CommandExecutor, TabCompleter {
    private ExtendedEconomy plugin;

    public ExtendedEconomyCommand(ExtendedEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("extendedeconomy.admin")) {
                    sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
                    sender.sendMessage(Message.PREFIX + "§acommands§8:");
                    sender.sendMessage(Message.PREFIX +
                            "  §8• §7/§extendedeconomy §7<§ahelp§7|§areload§7>");
                    sender.sendMessage(Message.PREFIX +
                            "  §8• §7/§aeconomy §7<§aset§7|§aadd§7|§atake§7|§ainfo§7> <§aPlayer§7> <§aAmount§7>\n§8(§7permission§8: §aextendedeconomy.command.economy§8)");
                    sender.sendMessage(Message.PREFIX +
                            "  §8• §7/§amoney §7<§ahelp§7>\n§8(§7permission§8: §aextendedeconomy.command.money§8)");
                    sender.sendMessage(Message.PREFIX +
                            "  §8• §7/§apay §7<§aPlayer§7> <§aAmount§7>\n§8(§7permission§8: §aextendedeconomy.command.pay§8)");
                    sender.sendMessage(Message.PREFIX +
                            "  §8• §7/§abalancetop\n§8(§7permission§8: §aextendedeconomy.command.balancetop§8)");
                    sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
                } else {
                    sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
                    sender.sendMessage(Message.PREFIX + "§acommands§8:");
                    sender.sendMessage(Message.PREFIX + "  §8• §7/§aextendedeconomy §7<§ahelp§7>");
                    sender.sendMessage(Message.PREFIX + "  §8• §7/§amoney");
                    sender.sendMessage(Message.PREFIX + "  §8• §7/§apay §7<§aPlayer§7> <§aAmount§7>");
                    sender.sendMessage(Message.PREFIX + "  §8• §7/§abalancetop");
                    sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!(sender.hasPermission("extendedeconomy.admin"))) {
                    sender.sendMessage(Message.getMessageWithPrefix(Message.NO_PERMISSION));
                    return true;
                }
                plugin.getConfigService().loadConfig();
                sender.sendMessage(Message.PREFIX + "reload is done!");
            } else {
                sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
                sender.sendMessage(Message.PREFIX +
                        "§a§lExtendedEconomy §7v§a" + StringUtil.replacePlaceholder(plugin.getDescription().getVersion(), Map.of(".", "§7.§a")));
                sender.sendMessage(Message.PREFIX + "§7made by §2JonaHD345 §8(§2https://jonahd345.de§8)");
                sender.sendMessage(Message.PREFIX + "§7for more §7/§aextendedeconomy help");
                sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
            }
        } else {
            sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
            sender.sendMessage(Message.PREFIX +
                    "§a§lExtendedEconomy §7v§a" + StringUtil.replacePlaceholder(plugin.getDescription().getVersion(), Map.of(".", "§7.§a")));
            sender.sendMessage(Message.PREFIX + "§7made by §2JonaHD345 §8(§2https://jonahd345.de§8)");
            sender.sendMessage(Message.PREFIX + "§7download §8(§2https://www.spigotmc.org/resources/extendedeconomy.106888/§8)");
            sender.sendMessage(Message.PREFIX + "§7for more §7/§aextendedeconomy help");
            sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> subcommand = new ArrayList<>();
        if (args.length == 1) {
            subcommand.add("help");
            if (sender.hasPermission("easyeconomy.admin")) {

            }
        }
        ArrayList<String> cl = new ArrayList<>();
        String currentarg = args[args.length - 1].toLowerCase();
        for(String s1 : subcommand) {
            String s2 = s1.toLowerCase();
            if(s2.startsWith(currentarg)) {
                cl.add(s1);
            }
        }

        return cl;
    }
}
