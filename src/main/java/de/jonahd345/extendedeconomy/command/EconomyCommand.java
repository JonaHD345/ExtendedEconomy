package de.jonahd345.extendedeconomy.command;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class EconomyCommand implements CommandExecutor, TabCompleter {
    private ExtendedEconomy plugin;

    public EconomyCommand(ExtendedEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("extendedeconomy.command.economy") || sender.hasPermission("extendedeconomy.admin"))) {
            sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + this.plugin.getCacheService().getMessages().get("messages.no_permission"));
            return true;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    this.plugin.getEconomyService().loadEconomyPlayer(offlinePlayer.getUniqueId());
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                            this.plugin.getCacheService().getMessages().get("messages.moneyother_message").replace("%Player%", offlinePlayer.getName()).replace("%Amount%",
                            String.valueOf(this.plugin.getNumber().formatNumber(this.plugin.getEconomy().getBalance(offlinePlayer)))));
                    this.plugin.getEconomyService().pushEconomyPlayer(offlinePlayer.getUniqueId());
                    return true;
                }
                sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                        this.plugin.getCacheService().getMessages().get("messages.moneyother_message").replace("%Player%", target.getName()).replace("%Amount%",
                        String.valueOf(this.plugin.getNumber().formatNumber(this.plugin.getEconomy().getBalance(target)))));
            } else {
                sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + "Use /economy <set|add|take|info> <Player> <Amount>");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (!(this.plugin.getNumber().isDouble(args[2]))) {
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + this.plugin.getCacheService().getMessages().get("messages.no_number"));
                    return true;
                }
                double amount = Double.parseDouble(args[2]);
                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    this.plugin.getEconomyService().loadEconomyPlayer(offlinePlayer.getUniqueId());
                    this.plugin.getEconomy().withdrawPlayer(offlinePlayer, this.plugin.getEconomy().getBalance(offlinePlayer));
                    this.plugin.getEconomy().depositPlayer(offlinePlayer, amount);
                    this.plugin.getEconomyService().pushEconomyPlayer(offlinePlayer.getUniqueId());
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                            this.plugin.getCacheService().getMessages().get("messages.ecoset_message").replace("%Player%",
                            offlinePlayer.getName()).replace("%Amount%", this.plugin.getNumber().formatNumber(amount)));
                    return true;
                }
                this.plugin.getEconomy().withdrawPlayer(target, this.plugin.getEconomy().getBalance(target));
                this.plugin.getEconomy().depositPlayer(target, amount);
                sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                        this.plugin.getCacheService().getMessages().get("messages.ecoset_message").replace("%Player%", target.getName()).replace("%Amount%",
                        this.plugin.getNumber().formatNumber(amount)));
            } else if (args[0].equalsIgnoreCase("add")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (!(this.plugin.getNumber().isDouble(args[2]))) {
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + this.plugin.getCacheService().getMessages().get("messages.no_number"));
                    return true;
                }
                double amount = Double.parseDouble(args[2]);
                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    this.plugin.getEconomyService().loadEconomyPlayer(offlinePlayer.getUniqueId());
                    this.plugin.getEconomy().depositPlayer(offlinePlayer, amount);
                    this.plugin.getEconomyService().pushEconomyPlayer(offlinePlayer.getUniqueId());
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                            this.plugin.getCacheService().getMessages().get("messages.ecoadd_message").replace("%Player%",
                            offlinePlayer.getName()).replace("%Amount%", this.plugin.getNumber().formatNumber(amount)));
                    return true;
                }
                this.plugin.getEconomy().depositPlayer(target, amount);
                sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                        this.plugin.getCacheService().getMessages().get("messages.ecoadd_message").replace("%Player%", target.getName()).replace("%Amount%",
                        this.plugin.getNumber().formatNumber(amount)));
            } else if (args[0].equalsIgnoreCase("take")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (!(this.plugin.getNumber().isDouble(args[2]))) {
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + this.plugin.getCacheService().getMessages().get("messages.no_number"));
                    return true;
                }
                double amount = Double.parseDouble(args[2]);
                if (target == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    this.plugin.getEconomyService().loadEconomyPlayer(offlinePlayer.getUniqueId());
                    if (this.plugin.getEconomy().getBalance(offlinePlayer) - amount < 0) {
                        sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + "You cannot set the balance below 0!");
                        return true;
                    }
                    this.plugin.getEconomy().withdrawPlayer(offlinePlayer, amount);
                    this.plugin.getEconomyService().pushEconomyPlayer(offlinePlayer.getUniqueId());
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                            this.plugin.getCacheService().getMessages().get("messages.ecotake_message").replace("%Player%",
                            offlinePlayer.getName()).replace("%Amount%", this.plugin.getNumber().formatNumber(amount)));
                    return true;
                }
                if (this.plugin.getEconomy().getBalance(target) - amount < 0) {
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + "You cannot set the balance below 0!");
                    return true;
                }
                this.plugin.getEconomy().withdrawPlayer(target, amount);
                sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                        this.plugin.getCacheService().getMessages().get("messages.ecotake_message").replace("%Player%", target.getName()).replace("%Amount%",
                        this.plugin.getNumber().formatNumber(amount)));
            } else {
                sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + "Use /economy <set|add|take|info> <Player> <Amount>");
            }
        } else {
            sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + "Use /economy <set|add|take|info> <Player> <Amount>");
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
