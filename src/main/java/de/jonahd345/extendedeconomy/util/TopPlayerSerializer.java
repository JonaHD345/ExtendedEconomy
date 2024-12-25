package de.jonahd345.extendedeconomy.util;


import de.jonahd345.extendedeconomy.model.EconomyTopPlayer;

import java.util.UUID;

public class TopPlayerSerializer {

    public String setTopPlayer(EconomyTopPlayer economyTopPlayer) {
        return economyTopPlayer.getUuid() + ";" + economyTopPlayer.getCoins();
    }

    public EconomyTopPlayer getTopPlayer(String input) {
        return new EconomyTopPlayer(UUID.fromString(input.split(";")[0]), new UUIDFetcher().getNameByUniqueId(UUID.fromString(input.split(";")[0])),
                Double.parseDouble(input.split(";")[1]));
    }
}
