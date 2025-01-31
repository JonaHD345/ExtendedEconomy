package de.jonahd345.extendedeconomy.command;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Leaderboard;
import de.jonahd345.extendedeconomy.config.Message;
import de.jonahd345.extendedeconomy.model.EconomyTopPlayer;
import de.jonahd345.extendedeconomy.util.NumberUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BalanceTopCommand implements CommandExecutor {
    private ExtendedEconomy plugin;

    public BalanceTopCommand(ExtendedEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("extendedeconomy.command.balancetop") || sender.hasPermission("extendedeconomy.admin"))) {
            sender.sendMessage(Message.getMessageWithPrefix(Message.NO_PERMISSION));
            return true;
        }

        sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
        sender.sendMessage(Leaderboard.getLeaderboardWithMessagePrefix(Leaderboard.HEADLINE));
        int place = 1;
        for (EconomyTopPlayer topPlayer : this.plugin.getEconomyTopPlayer()) {
            if (place < Leaderboard.SIZE.getValueAsInt()) {
                if (place == 1) {
                    sender.sendMessage(Leaderboard.getLeaderboardWithMessagePrefix(Leaderboard.PLACE_ONE).replace("%Player%", topPlayer.getName()).replace("%Amount%",
                                    NumberUtil.formatNumber(topPlayer.getCoins())));
                } else if (place == 2) {
                    sender.sendMessage(Leaderboard.getLeaderboardWithMessagePrefix(Leaderboard.PLACE_TWO).replace("%Player%", topPlayer.getName()).replace("%Amount%",
                                    NumberUtil.formatNumber(topPlayer.getCoins())));

                } else if (place == 3) {
                    sender.sendMessage(Leaderboard.getLeaderboardWithMessagePrefix(Leaderboard.PLACE_THREE).replace("%Player%", topPlayer.getName()).replace("%Amount%",
                                    NumberUtil.formatNumber(topPlayer.getCoins())));
                } else {
                    sender.sendMessage(Leaderboard.getLeaderboardWithMessagePrefix(Leaderboard.PLACE_OTHER).replace("%Player%", topPlayer.getName()).replace("%Amount%",
                                    NumberUtil.formatNumber(topPlayer.getCoins())).replace("%Place%",
                                    String.valueOf(place)));
                }
                place++;
            }
        }
        sender.sendMessage(Message.getMessageWithPrefix(Message.LINE));
        return false;
    }
}
