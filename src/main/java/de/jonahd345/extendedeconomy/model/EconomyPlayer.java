package de.jonahd345.extendedeconomy.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class EconomyPlayer {
    private UUID uuid;

    @Setter
    private double coins;

    @Setter
    private double bank;

    public EconomyPlayer(UUID uuid, double coins, double bank) {
        this.uuid = uuid;
        this.coins = coins;
        this.bank = bank;
    }

    public EconomyPlayer(UUID uuid) {
        this(uuid, 0L, 0L);
    }
}
