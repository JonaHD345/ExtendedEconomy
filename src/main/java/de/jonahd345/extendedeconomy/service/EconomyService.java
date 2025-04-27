package de.jonahd345.extendedeconomy.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Config;
import de.jonahd345.extendedeconomy.model.EconomyPlayer;
import de.jonahd345.extendedeconomy.model.EconomyTopPlayer;
import de.jonahd345.extendedeconomy.util.UUIDFetcher;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EconomyService {
    private ExtendedEconomy plugin;

    @Getter
    private Map<UUID, EconomyPlayer> economyPlayer;

    @Getter
    private List<EconomyTopPlayer> economyTopPlayer;

    private Gson gson;

    public EconomyService(ExtendedEconomy plugin) {
        this.plugin = plugin;
        this.economyPlayer = new HashMap<>();
        this.economyTopPlayer = new ArrayList<>();
        this.gson = new Gson();

        convertTopPlayersYamlToJson();
        convertPlayersYamlToSqlite();
    }

    public EconomyPlayer getEconomyPlayer(UUID uuid) {
        if (economyPlayer.containsKey(uuid)) {
            return economyPlayer.get(uuid);
        }
        return getEconomyPlayerSQL(uuid);
    }

    public void loadEconomyPlayer(UUID uuid) {
        if (!economyPlayer.containsKey(uuid)) {
            economyPlayer.put(uuid, getEconomyPlayerSQL(uuid));
        }
    }

    public void insertEconomyPlayer(EconomyPlayer economyPlayer) {
        insertEconomyPlayerSQL(economyPlayer);
    }

    public void updateEconomyPlayer(EconomyPlayer economyPlayer) {
        updateEconomyPlayerSQL(economyPlayer, false);
    }

    public void updateEconomyPlayer(UUID uuid) {
        updateEconomyPlayer(getEconomyPlayer(uuid));
    }

    public void updateEconomyPlayer(EconomyPlayer economyPlayer, boolean removePlayerFromMap) {
        updateEconomyPlayerSQL(economyPlayer, removePlayerFromMap);
    }

    public boolean isEconomyPlayerExists(UUID uuid) {
        return economyPlayer.containsKey(uuid) || isEconomyPlayerExistsSQL(uuid);
    }

    private EconomyPlayer getEconomyPlayerSQL(UUID uuid) {
        try (Connection connection = plugin.getDatabaseProvider().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM extendedeconomy_coins WHERE uuid = ?;")) {
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new EconomyPlayer(uuid, resultSet.getDouble("coins"));
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
        // Not found in MySQL DB / SQLite DB, create a new EconomyPlayer with default coins
        EconomyPlayer newEconomyPlayer = new EconomyPlayer(uuid, Config.STARTCOINS.getValueAsDouble());

        insertEconomyPlayerSQL(newEconomyPlayer);
        return newEconomyPlayer;
    }

    private void insertEconomyPlayerSQL(EconomyPlayer economyPlayer) {
        try (Connection connection = plugin.getDatabaseProvider().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO extendedeconomy_coins VALUES(?,?);")) {
            preparedStatement.setString(1, economyPlayer.getUuid().toString());
            preparedStatement.setDouble(2, economyPlayer.getCoins());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
    }

    private void updateEconomyPlayerSQL(EconomyPlayer economyPlayer, boolean removeFromMap) {
        try (Connection connection = plugin.getDatabaseProvider().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE extendedeconomy_coins SET coins = ? WHERE uuid = ?;")) {
            preparedStatement.setDouble(1, economyPlayer.getCoins());
            preparedStatement.setString(2, economyPlayer.getUuid().toString());
            preparedStatement.executeUpdate();

            if (removeFromMap) {
                this.economyPlayer.remove(economyPlayer.getUuid());
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
    }

    private boolean isEconomyPlayerExistsSQL(UUID uuid) {
        try (Connection connection = plugin.getDatabaseProvider().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM extendedeconomy_coins WHERE uuid = ?;")) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
        return false;
    }

    private void convertPlayersYamlToSqlite() {
        File filePlayers = new File("plugins/" + plugin.getName() + "/coins/coins.yml");
        YamlConfiguration yamlConfigurationPlayers = YamlConfiguration.loadConfiguration(filePlayers);

        if (filePlayers.exists()) {
            for (String uuid : yamlConfigurationPlayers.getKeys(false)) {
                insertEconomyPlayerSQL(new EconomyPlayer(UUID.fromString(uuid), yamlConfigurationPlayers.getDouble(uuid)));
            }
            filePlayers.renameTo(new File("plugins/" + plugin.getName() + "/coins/coinsOld.yml"));
        }
    }




    private void loadTopPlayersFromYaml(File file) {
        YamlConfiguration yamlConfigurationTopPlayers = YamlConfiguration.loadConfiguration(file);
        List<EconomyTopPlayer> list = new ArrayList<>();

        if (yamlConfigurationTopPlayers.isSet("leaderboard")) {
            for (String economyTopPlayer : yamlConfigurationTopPlayers.getStringList("leaderboard")) {
                list.add(plugin.getTopPlayerSerializer().getTopPlayer(economyTopPlayer));
            }
        }
        list = list.stream().sorted((o1, o2) -> Double.compare(o2.getCoins(), o1.getCoins())).collect(Collectors.toList());
        economyTopPlayer.addAll(list);
    }

    public void loadTopPlayers() {
        File file = new File("plugins/" + plugin.getName() + "/leaderboard.json");

        if (!file.exists()) {
            pushTopPlayers(); // Create the file if it doesn't exist
        }
        try (FileReader reader = new FileReader(file)) {
            economyTopPlayer.addAll(gson.fromJson(reader, new TypeToken<List<EconomyTopPlayer>>() {}.getType()));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
    }

    public void refreshTopPlayers() {
        List<EconomyTopPlayer> list = economyTopPlayer;
        List<EconomyPlayer> economyPlayers = new ArrayList<>(economyPlayer.values());

        for (EconomyPlayer economyPlayer : economyPlayers) {
            list.removeIf(economyTopPlayer -> economyTopPlayer.getUuid().equals(economyPlayer.getUuid()));
            double coins = economyPlayer.getCoins();
            list.add(new EconomyTopPlayer(economyPlayer.getUuid(), new UUIDFetcher().getNameByUniqueId(economyPlayer.getUuid()), coins));
        }
        list = list.stream().sorted((o1, o2) -> Double.compare(o2.getCoins(), o1.getCoins())).collect(Collectors.toList());
        economyTopPlayer.clear();
        economyTopPlayer.addAll(list);
    }

    public void pushTopPlayers() {
        try (FileWriter writer = new FileWriter("plugins/" + plugin.getName() + "/leaderboard.json")) {
            gson.toJson(economyTopPlayer, new TypeToken<List<EconomyTopPlayer>>() {}.getType(), writer);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
        economyTopPlayer.clear();
    }

    private void convertTopPlayersYamlToJson() {
        File fileTopPlayers = new File("plugins/" + plugin.getName() + "/leaderboard.yml");

        if (fileTopPlayers.exists()) {
            loadTopPlayersFromYaml(fileTopPlayers);
            fileTopPlayers.renameTo(new File("plugins/" + plugin.getName() + "/leaderboardOld.yml"));
        }
    }
}
