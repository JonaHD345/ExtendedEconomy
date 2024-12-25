package de.jonahd345.extendedeconomy.service;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CacheService {
    private ExtendedEconomy plugin;

    private File file;
    private FileConfiguration yamlConfiguration;

    public static boolean MYSQL;

    public Map<String, String> messages;

    public CacheService(ExtendedEconomy plugin) {
        this.plugin = plugin;
        this.file = new File("plugins/" + this.plugin.getName() + "/config.yml");
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);
        this.messages = new HashMap<>();
        this.checkFileExists();
        this.updateFromAnOlderVersion();
    }

    public void loadCache() {
        this.messages.clear();
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);

        MYSQL = this.yamlConfiguration.getBoolean("config.mysql");

        for (String key : this.yamlConfiguration.getKeys(true)) {
            if (!(key.equalsIgnoreCase("config") || key.equalsIgnoreCase("mysql") || key.equalsIgnoreCase("messages") ||
                    key.equalsIgnoreCase("leaderboard"))) {
                this.messages.put(key, this.translateColorCodes(this.yamlConfiguration.getString(key)));
            }
        }
    }

    private void checkFileExists() {
        if (!(this.file.exists())) {
            this.yamlConfiguration.set("config.mysql", false);
            this.yamlConfiguration.set("config.startcoins", "1000");

            this.yamlConfiguration.set("mysql.host", "127.0.0.1");
            this.yamlConfiguration.set("mysql.port", "3306");
            this.yamlConfiguration.set("mysql.user", "root");
            this.yamlConfiguration.set("mysql.password", "iamcool");
            this.yamlConfiguration.set("mysql.database", "extendedeconomy");

            this.yamlConfiguration.set("messages.prefix", "&a&lEXTENDEDECONOMY §8» &7");
            this.yamlConfiguration.set("messages.no_permission", "No permission!");
            this.yamlConfiguration.set("messages.no_playermessage", "You must be a player!");
            this.yamlConfiguration.set("messages.playernotfound", "Player not found!");
            this.yamlConfiguration.set("messages.no_number", "You have to enter a number!");
            this.yamlConfiguration.set("messages.no_money", "You haven't enough money!");
            this.yamlConfiguration.set("messages.pay_message", "You have %Player% payed %Amount%!");
            this.yamlConfiguration.set("messages.getmoney_message", "You received %Amount% from %Player%!");
            this.yamlConfiguration.set("messages.money_message", "You have %Amount% coins!");
            this.yamlConfiguration.set("messages.moneyother_message", "The %Player% have %Amount% coins!");
            this.yamlConfiguration.set("messages.pay_exeption", "You can't pay yourself!");
            this.yamlConfiguration.set("messages.ecoset_message", "You set %Player%'s balance %Amount%!");
            this.yamlConfiguration.set("messages.ecoadd_message", "You add %Player%'s balance %Amount%!");
            this.yamlConfiguration.set("messages.ecotake_message", "You took %Amount% from %Player%!");
            this.yamlConfiguration.set("messages.error", "ERROR");
            this.yamlConfiguration.set("messages.line", "&8&m---------------------------------------");

            this.yamlConfiguration.set("leaderboard.size", "5");
            this.yamlConfiguration.set("leaderboard.headline", "&2&lLEADERBOARD");
            this.yamlConfiguration.set("leaderboard.place_one", "&61&7. &a%Player% &7with &2%Amount% &aCoins");
            this.yamlConfiguration.set("leaderboard.place_two", "&72&7. &a%Player% &7with &2%Amount% &aCoins");
            this.yamlConfiguration.set("leaderboard.place_three", "&e3&7. &a%Player% &7with &2%Amount% &aCoins");
            this.yamlConfiguration.set("leaderboard.place_other", "&f%Place%&7. &a%Player% &7with &2%Amount% &aCoins");

            this.saveFile();
        }
    }

    private void updateFromAnOlderVersion() {
        //v1.2
        if (!(this.yamlConfiguration.isSet("messages.error"))) {
            this.yamlConfiguration.set("messages.error", "ERROR");
            this.yamlConfiguration.set("messages.line", "&8&m---------------------------------------");
            this.yamlConfiguration.set("leaderboard.size", "5");
            this.yamlConfiguration.set("leaderboard.headline", "&2&lLEADERBOARD");
            this.yamlConfiguration.set("leaderboard.place_one", "&61&7. &a%Player% &7with &2%Amount% &aCoins");
            this.yamlConfiguration.set("leaderboard.place_two", "&72&7. &a%Player% &7with &2%Amount% &aCoins");
            this.yamlConfiguration.set("leaderboard.place_three", "&e3&7. &a%Player% &7with &2%Amount% &aCoins");
            this.yamlConfiguration.set("leaderboard.place_other", "&f%Place%&7. &a%Player% &7with &2%Amount% &aCoins");
            this.saveFile();
        }
    }

    public void saveFile() {
        try {
            this.yamlConfiguration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String translateColorCodes(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public Map<String, String> getMessages() {
        return messages;
    }
}
