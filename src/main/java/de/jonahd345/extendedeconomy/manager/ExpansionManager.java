package de.jonahd345.extendedeconomy.manager;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Leaderboard;
import de.jonahd345.extendedeconomy.model.EconomyTopPlayer;
import de.jonahd345.extendedeconomy.util.NumberUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.List;

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
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String parameter) {
        if (parameter == null || player == null) {
            return "%" + getIdentifier() + "_" + parameter + "%";
        }

        switch (parameter.toLowerCase()) {
            case "user_balance":
                return NumberUtil.formatNumber(plugin.getEconomyService().getEconomyPlayer(player.getUniqueId()).getCoins());
            case "user_bank_balance":
                return NumberUtil.formatNumber(plugin.getEconomyService().getEconomyPlayer(player.getUniqueId()).getBank());
            case "leaderboard_headline":
                return Leaderboard.HEADLINE.getValueAsString();
            case "leaderboard_blank":
                return " ";
            default:
                break;
        }

        // leaderboard_place_<n>
        String prefix = "leaderboard_place_";

        if (!parameter.toLowerCase().startsWith(prefix)) {
            return "%" + getIdentifier() + "_" + parameter + "%";
        }
        String placeStr = parameter.substring(prefix.length());

        if (!NumberUtil.isInt(placeStr)) {
            return "%" + getIdentifier() + "_" + parameter + "%";
        }
        int place = Integer.parseInt(placeStr);
        boolean top3 = place <= 3;
        String template = top3 ? Leaderboard.valueOf("place_" + this.place[place - 1]).getValueAsString() : Leaderboard.PLACE_OTHER.getValueAsString();
        // get data
        String playerName = "null";
        String amount = "0";
        List<EconomyTopPlayer> topPlayers = plugin.getEconomyService().getEconomyTopPlayer();

        if (place >= 1 && topPlayers.size() >= place) {
            EconomyTopPlayer econPlayer = topPlayers.get(place - 1);

            playerName = econPlayer.getName();
            amount = NumberUtil.formatNumber(econPlayer.getCoins());
        }
        return template.replace("%Player%", playerName).replace("%Amount%", amount).replace("%Place%", String.valueOf(place));
    }

}
