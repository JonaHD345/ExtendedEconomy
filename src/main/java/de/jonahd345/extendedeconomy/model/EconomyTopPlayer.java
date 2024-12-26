package de.jonahd345.extendedeconomy.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class EconomyTopPlayer {
    private UUID uuid;

    private String name;

    private double coins;

    public EconomyTopPlayer(UUID uuid, String name, double coins) {
        this.uuid = uuid;
        this.name = name;
        this.coins = coins;
    }
}
