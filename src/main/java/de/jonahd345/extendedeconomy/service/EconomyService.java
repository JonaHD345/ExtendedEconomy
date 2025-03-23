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
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private Type listTypeTopPlayers;

    public EconomyService(ExtendedEconomy plugin) {
        this.plugin = plugin;
        this.economyPlayer = new HashMap<>();
        this.economyTopPlayer = new ArrayList<>();
        this.gson = new Gson();
        this.listTypeTopPlayers = new TypeToken<List<EconomyTopPlayer>>() {}.getType();
        this.convertTopPlayersYamlToJson();
        this.convertPlayersYamlToSqlite();
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
        try (PreparedStatement preparedStatement = this.plugin.getDatabaseProvider().getConnection().prepareStatement("SELECT * FROM extendedeconomy_coins WHERE uuid = ?;")) {
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new EconomyPlayer(uuid, resultSet.getDouble("coins"));
                }
            }
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
        // Not found in MySQL DB
        EconomyPlayer newEconomyPlayer = new EconomyPlayer(uuid, Config.STARTCOINS.getValueAsDouble());

        this.insertEconomyPlayerSQL(newEconomyPlayer);
        return newEconomyPlayer;
    }

    private void insertEconomyPlayerSQL(EconomyPlayer economyPlayer) {
        try (PreparedStatement preparedStatement = this.plugin.getDatabaseProvider().getConnection().prepareStatement("INSERT INTO extendedeconomy_coins VALUES(?,?);")) {
            preparedStatement.setString(1, economyPlayer.getUuid().toString());
            preparedStatement.setDouble(2, economyPlayer.getCoins());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
    }

    private void updateEconomyPlayerSQL(EconomyPlayer economyPlayer, boolean removeFromMap) {
        try (PreparedStatement preparedStatement = this.plugin.getDatabaseProvider().getConnection().prepareStatement("UPDATE extendedeconomy_coins SET coins = ? WHERE uuid = ?;")) {
            preparedStatement.setDouble(1, economyPlayer.getCoins());
            preparedStatement.setString(2, economyPlayer.getUuid().toString());
            preparedStatement.executeUpdate();

            if (removeFromMap) {
                this.economyPlayer.remove(economyPlayer.getUuid());
            }
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
    }

    private boolean isEconomyPlayerExistsSQL(UUID uuid) {
        try (PreparedStatement statement = this.plugin.getDatabaseProvider().getConnection().prepareStatement("SELECT * FROM extendedeconomy_coins WHERE uuid = ?;")) {
            statement.setObject(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            this.plugin.getLogger().severe("Error while checking if UUID is in database: " + e.getMessage());
        }
        return false;
    }

    private void convertPlayersYamlToSqlite() {
        File filePlayers = new File("plugins/" + this.plugin.getName() + "/coins/coins.yml");
        YamlConfiguration yamlConfigurationPlayers = YamlConfiguration.loadConfiguration(filePlayers);

        if (filePlayers.exists()) {
            for (String uuid : yamlConfigurationPlayers.getKeys(false)) {
                this.insertEconomyPlayerSQL(new EconomyPlayer(UUID.fromString(uuid), yamlConfigurationPlayers.getDouble(uuid)));
            }
            filePlayers.renameTo(new File("plugins/" + this.plugin.getName() + "/coins/coinsOld.yml"));
        }
    }




    private void loadTopPlayersFromYaml(File file) {
        YamlConfiguration yamlConfigurationTopPlayers = YamlConfiguration.loadConfiguration(file);
        List<EconomyTopPlayer> list = new ArrayList<>();

        if (yamlConfigurationTopPlayers.isSet("leaderboard")) {
            for (String economyTopPlayer : yamlConfigurationTopPlayers.getStringList("leaderboard")) {
                list.add(this.plugin.getTopPlayerSerializer().getTopPlayer(economyTopPlayer));
            }
        }
        list = list.stream().sorted((o1, o2) -> Double.compare(o2.getCoins(), o1.getCoins())).collect(Collectors.toList());
        this.economyTopPlayer.addAll(list);
    }

    public void loadTopPlayers() {
        File file = new File("plugins/" + this.plugin.getName() + "/leaderboard.json");

        if (!file.exists()) {
            this.pushTopPlayers(); // Create the file if it doesn't exist
        }
        try (FileReader reader = new FileReader(file)) {
            this.economyTopPlayer.addAll(this.gson.fromJson(reader, this.listTypeTopPlayers));
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
    }

    public void refreshTopPlayers() {
        List<EconomyTopPlayer> list = this.economyTopPlayer;
        List<EconomyPlayer> economyPlayers = new ArrayList<>(this.economyPlayer.values());

        for (EconomyPlayer economyPlayer : economyPlayers) {
            list.removeIf(economyTopPlayer -> economyTopPlayer.getUuid().equals(economyPlayer.getUuid()));
            double coins = economyPlayer.getCoins();
            list.add(new EconomyTopPlayer(economyPlayer.getUuid(), new UUIDFetcher().getNameByUniqueId(economyPlayer.getUuid()), coins));
        }
        list = list.stream().sorted((o1, o2) -> Double.compare(o2.getCoins(), o1.getCoins())).collect(Collectors.toList());
        this.economyTopPlayer.clear();
        this.economyTopPlayer.addAll(list);
    }

    public void pushTopPlayers() {
        try (FileWriter writer = new FileWriter("plugins/" + this.plugin.getName() + "/leaderboard.json")) {
            this.gson.toJson(this.economyTopPlayer, this.listTypeTopPlayers, writer);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
        this.economyTopPlayer.clear();
    }

    private void convertTopPlayersYamlToJson() {
        File fileTopPlayers = new File("plugins/" + this.plugin.getName() + "/leaderboard.yml");

        if (fileTopPlayers.exists()) {
            this.loadTopPlayersFromYaml(fileTopPlayers);
            fileTopPlayers.renameTo(new File("plugins/" + this.plugin.getName() + "/leaderboardOld.yml"));
        }
    }
}
