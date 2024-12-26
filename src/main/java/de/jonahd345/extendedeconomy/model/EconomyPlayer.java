package de.jonahd345.extendedeconomy.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class EconomyPlayer {
    private UUID uuid;

    @Setter
    private double coins;

    public EconomyPlayer(UUID uuid, double coins) {
        this.uuid = uuid;
        this.coins = coins;
    }

    public EconomyPlayer(UUID uuid) {
        this(uuid, 0L);
    }
}
