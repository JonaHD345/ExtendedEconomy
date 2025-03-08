package de.jonahd345.extendedeconomy.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Config;
import de.jonahd345.extendedeconomy.model.EconomyPlayer;
import de.jonahd345.extendedeconomy.model.EconomyTopPlayer;
import de.jonahd345.extendedeconomy.util.UUIDFetcher;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EconomyService {
    private ExtendedEconomy plugin;

    private Gson gson;

    private Type listTypeTopPlayers;

    public EconomyService(ExtendedEconomy plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        this.listTypeTopPlayers = new TypeToken<List<EconomyTopPlayer>>() {}.getType();
        this.convertTopPlayersYamlToJson();
        this.convertPlayersYamlToSqlite();
    }

    public void loadEconomyPlayer(UUID uuid) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = this.plugin.getDatabaseProvider().getConnection().prepareStatement("SELECT * FROM extendedeconomy_coins WHERE uuid = ?;");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                this.plugin.getEconomyPlayer().put(uuid, new EconomyPlayer(uuid, resultSet.getDouble("coins")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (!(this.plugin.getEconomyPlayer().containsKey(uuid))) {
            this.plugin.getEconomyPlayer().put(uuid, new EconomyPlayer(uuid, Config.STARTCOINS.getValueAsDouble()));
            this.pushEconomyPlayer(uuid);
        }
    }

    public void pushEconomyPlayer(UUID uuid) {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = this.plugin.getDatabaseProvider().getConnection().prepareStatement("INSERT INTO extendedeconomy_coins VALUES(?,?);");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setDouble(2, this.plugin.getEconomyPlayer().get(uuid).getCoins());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateEconomyPlayer(UUID uuid) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = this.plugin.getDatabaseProvider().getConnection().prepareStatement("UPDATE extendedeconomy_coins SET coins = ? WHERE uuid = ?;");
            preparedStatement.setDouble(1, this.plugin.getEconomyPlayer().get(uuid).getCoins());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void convertPlayersYamlToSqlite() {
        File filePlayers = new File("plugins/" + this.plugin.getName() + "/coins/coins.yml");
        YamlConfiguration yamlConfigurationPlayers = YamlConfiguration.loadConfiguration(filePlayers);

        if (filePlayers.exists()) {
            for (String uuid : yamlConfigurationPlayers.getKeys(false)) {
                this.plugin.getEconomyPlayer().put(UUID.fromString(uuid), new EconomyPlayer(UUID.fromString(uuid), yamlConfigurationPlayers.getDouble(uuid)));
                this.pushEconomyPlayer(UUID.fromString(uuid));
            }
            filePlayers.renameTo(new File("plugins/" + this.plugin.getName() + "/coins/coinsOld.yml"));
        }
    }

    private void convertTopPlayersYamlToJson() {
        File fileTopPlayers = new File("plugins/" + this.plugin.getName() + "/leaderboard.yml");

        if (fileTopPlayers.exists()) {
            this.loadTopPlayersFromYaml(fileTopPlayers);
            fileTopPlayers.renameTo(new File("plugins/" + this.plugin.getName() + "/leaderboardOld.yml"));
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
        this.plugin.getEconomyTopPlayer().addAll(list);
    }

    public void loadTopPlayers() {
        File file = new File("plugins/" + this.plugin.getName() + "/leaderboard.json");

        if (!file.exists()) {
            this.pushTopPlayers(); // Create the file if it doesn't exist
        }
        try (FileReader reader = new FileReader(file)) {
            this.plugin.getEconomyTopPlayer().addAll(this.gson.fromJson(reader, this.listTypeTopPlayers));
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
    }

    public void refreshTopPlayers() {
        List<EconomyTopPlayer> list = this.plugin.getEconomyTopPlayer();
        List<EconomyPlayer> economyPlayers = new ArrayList<>(this.plugin.getEconomyPlayer().values());

        for (EconomyPlayer economyPlayer : economyPlayers) {
            list.removeIf(economyTopPlayer -> economyTopPlayer.getUuid().equals(economyPlayer.getUuid()));
            double coins = economyPlayer.getCoins();
            list.add(new EconomyTopPlayer(economyPlayer.getUuid(), new UUIDFetcher().getNameByUniqueId(economyPlayer.getUuid()), coins));
        }
        list = list.stream().sorted((o1, o2) -> Double.compare(o2.getCoins(), o1.getCoins())).collect(Collectors.toList());
        this.plugin.getEconomyTopPlayer().clear();
        this.plugin.getEconomyTopPlayer().addAll(list);
    }

    public void pushTopPlayers() {
        try (FileWriter writer = new FileWriter("plugins/" + this.plugin.getName() + "/leaderboard.json")) {
            this.gson.toJson(this.plugin.getEconomyTopPlayer(), this.listTypeTopPlayers, writer);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
        }
        this.plugin.getEconomyTopPlayer().clear();
    }

    private void createFileIfNotExist(File file) {
        if (!(file.exists())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE,"An error occurred", e);
            }
        }
    }
}
