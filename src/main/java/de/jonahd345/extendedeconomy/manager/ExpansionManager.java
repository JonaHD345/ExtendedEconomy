package de.jonahd345.extendedeconomy.manager;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class ExpansionManager extends PlaceholderExpansion {
    private ExtendedEconomy plugin;

    private String place;

    public ExpansionManager(ExtendedEconomy plugin) {
        this.plugin = plugin;
        this.place = "one_two_three";
    }

    @Override
    public String getIdentifier() {
        return "extendedeconomy";
    }

    @Override
    public String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String parameter) {
        if(parameter.equalsIgnoreCase("user_balance")) {
            return this.plugin.getNumber().formatNumber(this.plugin.getEconomyPlayer().get(player.getUniqueId()).getCoins());
        }
        if (parameter.equalsIgnoreCase("leaderboard_headline")) {
            return this.plugin.getCacheService().getMessages().get("leaderboard.headline");
        }
        if (parameter.equalsIgnoreCase("leaderboard_blank")) {
            return " ";
        }
        if (parameter.contains("leaderboard_place_")) {
            if (this.plugin.getNumber().isInt(parameter.split("_")[2]) &&
                    this.plugin.getEconomyTopPlayer().size() >= Integer.parseInt(parameter.split("_")[2])) {
                if (Integer.parseInt(parameter.split("_")[2]) <= 3) {
                    return this.plugin.getCacheService().getMessages().get("leaderboard.place_" +
                            this.place.split("_")[Integer.parseInt(parameter.split("_")[2]) - 1]).replace("%Player%",
                            this.plugin.getEconomyTopPlayer().get(Integer.parseInt(parameter.split("_")[2]) - 1).getName()).replace("%Amount%",
                            this.plugin.getNumber().formatNumber(this.plugin.getEconomyTopPlayer().get(Integer.parseInt(parameter.split("_")[2]) - 1).getCoins()));
                } else {
                    return this.plugin.getCacheService().getMessages().get("leaderboard.place_other").replace("%Player%",
                                    this.plugin.getEconomyTopPlayer().get(Integer.parseInt(parameter.split("_")[2]) - 1).getName()).replace("%Amount%",
                                    this.plugin.getNumber().formatNumber(this.plugin.getEconomyTopPlayer().get(Integer.parseInt(parameter.split("_")[2]) - 1).getCoins()))
                            .replace("%Place%", parameter.split("_")[2]);
                }
            } else {
                if (Integer.parseInt(parameter.split("_")[2]) <= 3) {
                    return this.plugin.getCacheService().getMessages().get("leaderboard.place_" +
                                    this.place.split("_")[Integer.parseInt(parameter.split("_")[2]) - 1]).replace("%Player%", "null")
                            .replace("%Amount%", "0").replace("%Place%", parameter.split("_")[2]);
                } else {
                    return this.plugin.getCacheService().getMessages().get("leaderboard.place_other").replace("%Player%", "null")
                            .replace("%Amount%", "0").replace("%Place%", parameter.split("_")[2]);
                }
            }
        }

        return "%" + getIdentifier() + "_" + parameter + "%";
    }
}
