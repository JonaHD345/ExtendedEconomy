package de.jonahd345.extendedeconomy.service;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Config;
import de.jonahd345.extendedeconomy.model.EconomyPlayer;
import de.jonahd345.extendedeconomy.model.EconomyTopPlayer;
import de.jonahd345.extendedeconomy.util.FileUtil;
import de.jonahd345.extendedeconomy.util.UUIDFetcher;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EconomyService {
    private ExtendedEconomy plugin;

    private File file;

    private YamlConfiguration yamlConfiguration;

    public EconomyService(ExtendedEconomy plugin) {
        this.plugin = plugin;
        this.file = new File("plugins/" + this.plugin.getName() + "/coins/coins.yml");
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);
        this.checkFileExists(this.file, this.yamlConfiguration);
    }

    public void loadEconomyPlayer(UUID uuid) {
        if (Config.MYSQL.getValueAsBoolean()) {
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
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (this.yamlConfiguration.isSet(uuid.toString())) {
                this.plugin.getEconomyPlayer().put(uuid, new EconomyPlayer(uuid, this.yamlConfiguration.getLong(uuid.toString())));
            }
        }
        if (!(this.plugin.getEconomyPlayer().containsKey(uuid))) {
            this.plugin.getEconomyPlayer().put(uuid, new EconomyPlayer(uuid, Config.STARTCOINS.getValueAsDouble()));
        }
    }

    public void pushEconomyPlayer(UUID uuid, boolean removeFromList) {
        if (Config.MYSQL.getValueAsBoolean()) {
            PreparedStatement preparedStatement = null;
            delete(uuid);
            try {
                preparedStatement = this.plugin.getDatabaseProvider().getConnection().prepareStatement("INSERT INTO extendedeconomy_coins VALUES(?,?);");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setDouble(2, this.plugin.getEconomyPlayer().get(uuid).getCoins());
                preparedStatement.executeUpdate();
                if (removeFromList) {
                    this.plugin.getEconomyPlayer().remove(uuid);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            this.yamlConfiguration.set(uuid.toString(), this.plugin.getEconomyPlayer().get(uuid).getCoins());
            if (removeFromList) {
                this.plugin.getEconomyPlayer().remove(uuid);
            }
            FileUtil.saveFile(this.yamlConfiguration, this.file);
        }
    }

    public void pushEconomyPlayer(UUID uuid) {
        pushEconomyPlayer(uuid, false);
    }

    private void delete(UUID uuid) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.plugin.getDatabaseProvider().getConnection().prepareStatement("DELETE FROM extendedeconomy_coins WHERE uuid = ?;");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setupTopPlayers() {
        File fileTopPlayers = new File("plugins/" + this.plugin.getName() + "/leaderboard.yml");
        YamlConfiguration yamlConfigurationTopPlayers = YamlConfiguration.loadConfiguration(fileTopPlayers);
        List<EconomyTopPlayer> list = new ArrayList<>();

        checkFileExists(fileTopPlayers, yamlConfigurationTopPlayers);
        if (yamlConfigurationTopPlayers.isSet("leaderboard")) {
            for (String economyTopPlayer : yamlConfigurationTopPlayers.getStringList("leaderboard")) {
                list.add(this.plugin.getTopPlayerSerializer().getTopPlayer(economyTopPlayer));
            }
        }
        list = list.stream().sorted((o1, o2) -> Double.compare(o2.getCoins(), o1.getCoins())).collect(Collectors.toList());
        this.plugin.getEconomyTopPlayer().addAll(list);
    }

    public void loadTopPlayers() {
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
        File fileTopPlayers = new File("plugins/" + this.plugin.getName() + "/leaderboard.yml");
        YamlConfiguration yamlConfigurationTopPlayers = YamlConfiguration.loadConfiguration(fileTopPlayers);
        List<String> list = new ArrayList<>();

        for (EconomyTopPlayer economyTopPlayer : this.plugin.getEconomyTopPlayer()) {
            list.add(this.plugin.getTopPlayerSerializer().setTopPlayer(economyTopPlayer));
        }
        yamlConfigurationTopPlayers.set("leaderboard", list);
        FileUtil.saveFile(yamlConfigurationTopPlayers, fileTopPlayers);
        this.plugin.getEconomyTopPlayer().clear();
    }

    private void checkFileExists(File file, YamlConfiguration yamlConfiguration) {
        if (!(file.exists())) {
            FileUtil.saveFile(yamlConfiguration, file);
        }
    }
}
