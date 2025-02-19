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
import org.bukkit.entity.Player;

import java.util.Map;

public class MoneyCommand implements CommandExecutor {
    private ExtendedEconomy plugin;

    public MoneyCommand(ExtendedEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.getMessageWithPrefix(Message.NO_PLAYER));
            return true;
        }
        Player player = (Player) sender;

        if (!(player.hasPermission("extendedeconomy.command.money") || player.hasPermission("extendedeconomy.admin"))) {
            player.sendMessage(Message.getMessageWithPrefix(Message.NO_PERMISSION));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.MONEY),
                    Map.of("%Amount%", NumberUtil.formatNumber(this.plugin.getEconomy().getBalance(player)))));
        } else if (args.length == 1) {
            if (!(player.hasPermission("extendedeconomy.command.money.other"))) {
                player.chat("/money");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                this.plugin.getEconomyService().loadEconomyPlayer(offlinePlayer.getUniqueId());
                player.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.MONEY_OTHER),
                        Map.of("%Player%", offlinePlayer.getName(), "%Amount%", NumberUtil.formatNumber(this.plugin.getEconomy().getBalance(offlinePlayer)))));
                this.plugin.getEconomyService().pushEconomyPlayer(offlinePlayer.getUniqueId());
                return true;
            }
            player.sendMessage(StringUtil.replacePlaceholder(Message.getMessageWithPrefix(Message.MONEY_OTHER),
                    Map.of("%Player%", target.getName(), "%Amount%", NumberUtil.formatNumber(this.plugin.getEconomy().getBalance(target)))));
        }
        return false;
    }
}
