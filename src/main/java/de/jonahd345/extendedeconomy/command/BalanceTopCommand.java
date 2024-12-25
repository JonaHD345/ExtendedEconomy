package de.jonahd345.extendedeconomy.command;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.model.EconomyTopPlayer;
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
            sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + this.plugin.getCacheService().getMessages().get("messages.no_permission"));
            return true;
        }

        sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + this.plugin.getCacheService().getMessages().get("messages.line"));
        sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + this.plugin.getCacheService().getMessages().get("leaderboard.headline"));
        int place = 1;
        for (EconomyTopPlayer topPlayer : this.plugin.getEconomyTopPlayer()) {
            if (place < Integer.parseInt(this.plugin.getCacheService().getMessages().get("leaderboard.size"))) {
                if (place == 1) {
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                            this.plugin.getCacheService().getMessages().get("leaderboard.place_one").replace("%Player%", topPlayer.getName()).replace("%Amount%",
                                    this.plugin.getNumber().formatNumber(topPlayer.getCoins())));
                } else if (place == 2) {
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                            this.plugin.getCacheService().getMessages().get("leaderboard.place_two").replace("%Player%", topPlayer.getName()).replace("%Amount%",
                                    this.plugin.getNumber().formatNumber(topPlayer.getCoins())));

                } else if (place == 3) {
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                            this.plugin.getCacheService().getMessages().get("leaderboard.place_three").replace("%Player%", topPlayer.getName()).replace("%Amount%",
                                    this.plugin.getNumber().formatNumber(topPlayer.getCoins())));
                } else {
                    sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") +
                            this.plugin.getCacheService().getMessages().get("leaderboard.place_other").replace("%Player%", topPlayer.getName()).replace("%Amount%",
                                    this.plugin.getNumber().formatNumber(topPlayer.getCoins())).replace("%Place%",
                                    String.valueOf(place)));
                }
                place++;
            }
        }
        sender.sendMessage(this.plugin.getCacheService().getMessages().get("messages.prefix") + this.plugin.getCacheService().getMessages().get("messages.line"));
        return false;
    }
}
