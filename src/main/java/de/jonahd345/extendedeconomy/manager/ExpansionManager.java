package de.jonahd345.extendedeconomy.manager;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Leaderboard;
import de.jonahd345.extendedeconomy.util.NumberUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class ExpansionManager extends PlaceholderExpansion {
    private ExtendedEconomy plugin;

    private String[] place;

    public ExpansionManager(ExtendedEconomy plugin) {
        this.plugin = plugin;
        this.place = new String[]{"one", "two", "three"};
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
            return NumberUtil.formatNumber(this.plugin.getEconomyService().getEconomyPlayer().get(player.getUniqueId()).getCoins());
        }
        if (parameter.equalsIgnoreCase("leaderboard_headline")) {
            return Leaderboard.HEADLINE.getValueAsString();
        }
        if (parameter.equalsIgnoreCase("leaderboard_blank")) {
            return " ";
        }
        if (parameter.contains("leaderboard_place_")) {
            if (NumberUtil.isInt(parameter.split("_")[2]) &&
                    this.plugin.getEconomyService().getEconomyTopPlayer().size() >= Integer.parseInt(parameter.split("_")[2])) {
                if (Integer.parseInt(parameter.split("_")[2]) <= 3) {
                    return Leaderboard.valueOf("place_" + this.place[Integer.parseInt(parameter.split("_")[2]) - 1]).getValueAsString().replace("%Player%",
                            this.plugin.getEconomyService().getEconomyTopPlayer().get(Integer.parseInt(parameter.split("_")[2]) - 1).getName()).replace("%Amount%",
                            NumberUtil.formatNumber(this.plugin.getEconomyService().getEconomyTopPlayer().get(Integer.parseInt(parameter.split("_")[2]) - 1).getCoins()));
                } else {
                    return Leaderboard.PLACE_OTHER.getValueAsString().replace("%Player%",
                                    this.plugin.getEconomyService().getEconomyTopPlayer().get(Integer.parseInt(parameter.split("_")[2]) - 1).getName()).replace("%Amount%",
                                    NumberUtil.formatNumber(this.plugin.getEconomyService().getEconomyTopPlayer().get(Integer.parseInt(parameter.split("_")[2]) - 1).getCoins()))
                            .replace("%Place%", parameter.split("_")[2]);
                }
            } else {
                if (Integer.parseInt(parameter.split("_")[2]) <= 3) {
                    return Leaderboard.valueOf("place_" + this.place[Integer.parseInt(parameter.split("_")[2]) - 1]).getValueAsString().replace("%Player%", "null")
                            .replace("%Amount%", "0").replace("%Place%", parameter.split("_")[2]);
                } else {
                    return Leaderboard.PLACE_OTHER.getValueAsString().replace("%Player%", "null")
                            .replace("%Amount%", "0").replace("%Place%", parameter.split("_")[2]);
                }
            }
        }

        return "%" + getIdentifier() + "_" + parameter + "%";
    }
}
