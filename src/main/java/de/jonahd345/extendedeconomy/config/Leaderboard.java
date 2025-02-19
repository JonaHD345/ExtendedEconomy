package de.jonahd345.extendedeconomy.config;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Leaderboard {
    SIZE(5),
    HEADLINE("&2&lLEADERBOARD"),
    PLACE_ONE("&61&7. &a%Player% &7with &2%Amount% &aCoins"),
    PLACE_TWO("&72&7. &a%Player% &7with &2%Amount% &aCoins"),
    PLACE_THREE("&e3&7. &a%Player% &7with &2%Amount% &aCoins"),
    PLACE_OTHER("&f%Place%&7. &a%Player% &7with &2%Amount% &aCoins");

    private final Object defaultValue;
    @Setter
    private Object value;

    Leaderboard(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValueAsString() {
        return this.value.toString();
    }

    public Integer getDefaultValueAsInt() {
        return Integer.valueOf(this.defaultValue.toString());
    }

    public Integer getValueAsInt() {
        try { return Integer.valueOf(this.value.toString()); } catch (NumberFormatException e) { return this.getDefaultValueAsInt(); }
    }

    public static String getLeaderboardWithMessagePrefix(Leaderboard leaderboard) {
        return Message.PREFIX.getMessage() + leaderboard.getValue();
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
