package de.jonahd345.extendedeconomy.command;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Message;
import de.jonahd345.extendedeconomy.model.EconomyPlayer;
import de.jonahd345.extendedeconomy.util.NumberUtil;
import de.jonahd345.extendedeconomy.util.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class BankCommand implements CommandExecutor {
    private ExtendedEconomy plugin;

    public BankCommand(ExtendedEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.getMessageWithPrefix(Message.NO_PLAYER));
            return true;
        }
        Player player = (Player) sender;

        if (!(player.hasPermission("extendedeconomy.command.bank") || player.hasPermission("extendedeconomy.admin"))) {
            player.sendMessage(Message.getMessageWithPrefix(Message.NO_PERMISSION));
            return true;
        }
        EconomyPlayer economyPlayer = plugin.getEconomyService().getEconomyPlayer(player.getUniqueId());

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("deposit")) {
                if (!(NumberUtil.isDouble(args[1]))) {
                    player.sendMessage(Message.getMessageWithPrefix(Message.NO_NUMBER));
                    return true;
                }
                double amount = Double.parseDouble(args[1]);

                if (plugin.getEconomy().getBalance(player) < amount) {
                    player.sendMessage(Message.getMessageWithPrefix(Message.NO_MONEY));
                    return true;
                }

                plugin.getEconomy().withdrawPlayer(player, amount);
                economyPlayer.setBank(economyPlayer.getBank() + amount);
                player.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.BANK_DEPOSIT),
                        Map.of("%Amount%", NumberUtil.formatNumber(amount))));
            } else if (args[0].equalsIgnoreCase("withdraw")) {
                if (!(NumberUtil.isDouble(args[1]))) {
                    player.sendMessage(Message.getMessageWithPrefix(Message.NO_NUMBER));
                    return true;
                }
                double amount = Double.parseDouble(args[1]);

                if (economyPlayer.getBank() < amount) {
                    player.sendMessage("");
                    return true;
                }
                plugin.getEconomy().depositPlayer(player, amount);
                economyPlayer.setBank(economyPlayer.getBank() - amount);
                player.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.BANK_WITHDRAW),
                        Map.of("%Amount%", NumberUtil.formatNumber(amount))));
            } else {
                player.sendMessage(Message.getMessageWithPrefix(Message.BANK_HELP));
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("info")) {
                player.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.BANK_MONEY),
                    Map.of("%Amount%", NumberUtil.formatNumber(economyPlayer.getBank()))));
            } else if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage(Message.getMessageWithPrefix(Message.BANK_HELP));
            } else {
                player.sendMessage(Message.getMessageWithPrefix(Message.BANK_HELP));
            }
        } else {
            player.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.BANK_MONEY),
                    Map.of("%Amount%", NumberUtil.formatNumber(economyPlayer.getBank()))));
        }
        return false;
    }
}
