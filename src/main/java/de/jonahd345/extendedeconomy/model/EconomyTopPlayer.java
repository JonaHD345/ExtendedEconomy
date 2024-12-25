package de.jonahd345.extendedeconomy.model;

import java.util.UUID;

public class EconomyTopPlayer {
    private UUID uuid;

    private String name;

    private double coins;

    public EconomyTopPlayer(UUID uuid, String name, double coins) {
        this.uuid = uuid;
        this.name = name;
        this.coins = coins;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public double getCoins() {
        return this.coins;
    }
}
