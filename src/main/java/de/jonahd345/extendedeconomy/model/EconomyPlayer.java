package de.jonahd345.extendedeconomy.model;

import java.util.UUID;

public class EconomyPlayer {
    private UUID uuid;

    private double coins;

    public EconomyPlayer(UUID uuid, double coins) {
        this.uuid = uuid;
        this.coins = coins;
    }

    public EconomyPlayer(UUID uuid) {
        this(uuid, 0L);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public double getCoins() {
        return coins;
    }
}
