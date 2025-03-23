package de.jonahd345.extendedeconomy.command;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Message;
import de.jonahd345.extendedeconomy.util.NumberUtil;
import de.jonahd345.extendedeconomy.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EconomyCommand implements CommandExecutor, TabCompleter {
    private ExtendedEconomy plugin;

    public EconomyCommand(ExtendedEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("extendedeconomy.command.economy") || sender.hasPermission("extendedeconomy.admin"))) {
            sender.sendMessage(Message.getMessageWithPrefix(Message.NO_PERMISSION));
            return true;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

                    sender.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.MONEY_OTHER),
                            Map.of("%Player%", offlinePlayer.getName(), "%Amount%", NumberUtil.formatNumber(plugin.getEconomy().getBalance(offlinePlayer)))));
                    return true;
                }
                sender.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.MONEY_OTHER),
                        Map.of("%Player%", target.getName(), "%Amount%", NumberUtil.formatNumber(plugin.getEconomy().getBalance(target)))));
            } else {
                sender.sendMessage(Message.PREFIX + "§7Use /economy <set|add|take|info> <Player> <Amount>");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (!(NumberUtil.isDouble(args[2]))) {
                    sender.sendMessage(Message.getMessageWithPrefix(Message.NO_NUMBER));
                    return true;
                }
                double amount = Double.parseDouble(args[2]);

                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                    plugin.getEconomy().withdrawPlayer(offlinePlayer, plugin.getEconomy().getBalance(offlinePlayer));
                    plugin.getEconomy().depositPlayer(offlinePlayer, amount);
                    plugin.getEconomyService().updateEconomyPlayer(offlinePlayer.getUniqueId());
                    sender.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.MONEY_OTHER),
                            Map.of("%Player%", offlinePlayer.getName(), "%Amount%", NumberUtil.formatNumber(amount))));
                    return true;
                }
                plugin.getEconomy().withdrawPlayer(target, plugin.getEconomy().getBalance(target));
                plugin.getEconomy().depositPlayer(target, amount);
                sender.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.MONEY_OTHER),
                        Map.of("%Player%", target.getName(), "%Amount%", NumberUtil.formatNumber(amount))));
            } else if (args[0].equalsIgnoreCase("add")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (!(NumberUtil.isDouble(args[2]))) {
                    sender.sendMessage(Message.getMessageWithPrefix(Message.NO_NUMBER));
                    return true;
                }
                double amount = Double.parseDouble(args[2]);

                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    plugin.getEconomy().depositPlayer(offlinePlayer, amount);
                    plugin.getEconomyService().updateEconomyPlayer(offlinePlayer.getUniqueId());
                    sender.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.ECO_ADD),
                            Map.of("%Player%", offlinePlayer.getName(), "%Amount%", NumberUtil.formatNumber(amount))));
                    return true;
                }
                plugin.getEconomy().depositPlayer(target, amount);
                sender.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.ECO_ADD),
                        Map.of("%Player%", target.getName(), "%Amount%", NumberUtil.formatNumber(amount))));
            } else if (args[0].equalsIgnoreCase("take")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (!(NumberUtil.isDouble(args[2]))) {
                    sender.sendMessage(Message.getMessageWithPrefix(Message.NO_NUMBER));
                    return true;
                }
                double amount = Double.parseDouble(args[2]);

                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                    if (plugin.getEconomy().getBalance(offlinePlayer) - amount < 0) {
                        sender.sendMessage(Message.PREFIX + "§7You cannot set the balance below 0!");
                        return true;
                    }
                    plugin.getEconomy().withdrawPlayer(offlinePlayer, amount);
                    plugin.getEconomyService().updateEconomyPlayer(offlinePlayer.getUniqueId());
                    sender.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.ECO_TAKE),
                            Map.of("%Player%", offlinePlayer.getName(), "%Amount%", NumberUtil.formatNumber(amount))));
                    return true;
                }
                if (plugin.getEconomy().getBalance(target) - amount < 0) {
                    sender.sendMessage(Message.PREFIX + "§7You cannot set the balance below 0!");
                    return true;
                }
                plugin.getEconomy().withdrawPlayer(target, amount);
                sender.sendMessage(Message.getMessageWithPrefix(Message.ECO_TAKE).replace("%Player%", target.getName()).replace("%Amount%",
                        NumberUtil.formatNumber(amount)));
                sender.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.ECO_TAKE),
                        Map.of("%Player%", target.getName(), "%Amount%", NumberUtil.formatNumber(amount))));
            } else {
                sender.sendMessage(Message.PREFIX + "§7Use /economy <set|add|take|info> <Player> <Amount>");
            }
        } else {
            sender.sendMessage(Message.PREFIX + "§7Use /economy <set|add|take|info> <Player> <Amount>");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> subcommand = new ArrayList<>();

        if (sender.hasPermission("extendedeconomy.command.economy") || sender.hasPermission("extendedeconomy.admin")) {
            if (args.length == 1) {
                subcommand.add("set");
                subcommand.add("add");
                subcommand.add("take");
                subcommand.add("info");
            }
            if (args.length == 2) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    subcommand.add(all.getName());
                }
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
