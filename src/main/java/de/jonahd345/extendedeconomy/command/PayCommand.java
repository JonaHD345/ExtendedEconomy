package de.jonahd345.extendedeconomy.command;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Message;
import de.jonahd345.extendedeconomy.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PayCommand implements CommandExecutor, TabCompleter {
    private ExtendedEconomy plugin;

    public PayCommand(ExtendedEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.getMessageWithPrefix(Message.NO_PLAYER));
            return true;
        }
        Player player = (Player) sender;

        if (!(player.hasPermission("extendedeconomy.command.pay") || player.hasPermission("extendedeconomy.admin"))) {
            player.sendMessage(Message.getMessageWithPrefix(Message.NO_PERMISSION));
            return true;
        }
        if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[0]);

            if (!(NumberUtil.isDouble(args[1]))) {
                player.sendMessage(Message.getMessageWithPrefix(Message.NO_NUMBER));
                return true;
            }
            double amount = Double.parseDouble(args[1]);
            if (this.plugin.getEconomy().getBalance(player) < amount) {
                player.sendMessage(Message.getMessageWithPrefix(Message.NO_MONEY));
                return true;
            }
            if (args[0].equalsIgnoreCase("*")) {
                if (!(player.hasPermission("extendedeconomy.command.pay.*"))) {
                    player.sendMessage(Message.getMessageWithPrefix(Message.NO_PERMISSION));
                    return true;
                }
                if (Bukkit.getOnlinePlayers().size() <= 1) {
                    return true;
                }
                if (this.plugin.getEconomy().getBalance(player) < amount * Bukkit.getOnlinePlayers().size()) {
                    player.sendMessage(Message.getMessageWithPrefix(Message.NO_MONEY));
                    return true;
                }
                this.plugin.getEconomy().withdrawPlayer(player, amount * Bukkit.getOnlinePlayers().size());
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!(player == players)) {
                        this.plugin.getEconomy().depositPlayer(players, amount);
                       player.sendMessage(Message.getMessageWithPrefix(Message.PAY).replace("%Player%", players.getName()).replace("%Amount%",
                               NumberUtil.formatNumber(amount)).replace(",", "."));
                        players.sendMessage(Message.getMessageWithPrefix(Message.GET_MONEY).replace("%Player%",
                                player.getName()).replace("%Amount%", NumberUtil.formatNumber(amount)).replace(",", "."));
                    }
                }
                return true;
            }
            if (target == null) {
                player.sendMessage(Message.getMessageWithPrefix(Message.PLAYER_NOT_FOUND));
                return true;
            }
            if (player == target) {
                player.sendMessage(Message.getMessageWithPrefix(Message.PAY_EXCEPTION));
                return true;
            }
            this.plugin.getEconomy().withdrawPlayer(player, amount);
            this.plugin.getEconomy().depositPlayer(target, amount);
            player.sendMessage(Message.getMessageWithPrefix(Message.PAY).replace("%Player%", target.getName()).replace("%Amount%",
                    NumberUtil.formatNumber(amount)).replace(",", "."));
            target.sendMessage(Message.getMessageWithPrefix(Message.GET_MONEY).replace("%Player%", player.getName()).replace("%Amount%",
                    NumberUtil.formatNumber(amount)).replace(",", "."));
        } else {
            player.sendMessage(Message.PREFIX.getMessage() + "Use /pay <Player> <Amount>");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> subcommand = new ArrayList<>();
        if (sender.hasPermission("extendedeconomy.command.pay") || sender.hasPermission("extendedeconomy.admin")) {
            if (args.length == 1) {
                subcommand.add("*");
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
