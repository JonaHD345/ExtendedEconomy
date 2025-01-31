package de.jonahd345.extendedeconomy.service;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Config;
import de.jonahd345.extendedeconomy.config.Leaderboard;
import de.jonahd345.extendedeconomy.config.Message;
import de.jonahd345.extendedeconomy.util.FileUtil;
import de.jonahd345.extendedeconomy.util.StringUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class ConfigService {
    private ExtendedEconomy plugin;

    private File file;

    private FileConfiguration yamlConfiguration;

    public ConfigService(ExtendedEconomy plugin) {
        this.plugin = plugin;
        this.file = new File("plugins/" + this.plugin.getName() + "/config.yml");
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);
        this.checkFileExists();
    }

    public void loadConfig() {
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);
        boolean hasFileChanges = false;

        // Config
        for (Config config : Config.values()) {
            if (!(this.file.exists()) || this.yamlConfiguration.getString("config." + config.name().toLowerCase()) == null) {
                this.yamlConfiguration.set("config." + config.name().toLowerCase(), config.getValue().toString());
                hasFileChanges = true;
                // set Config's config to his default config and skip the next line, because by new mess yamlConfiguration.getString is null
                config.setValue(StringUtil.translateColorCodes(config.getDefaultValue().toString()));
                continue;
            }
            config.setValue(StringUtil.translateColorCodes(this.yamlConfiguration.getString("config." + config.name().toLowerCase())));
        }
        // Messages
        for (Message message : Message.values()) {
            if (!(this.file.exists()) || this.yamlConfiguration.getString("messages." + message.name().toLowerCase()) == null) {
                this.yamlConfiguration.set("messages." + message.name().toLowerCase(), message.getDefaultMessage());
                hasFileChanges = true;
                // set Message's message to his default message and skip the next line, because by new mess yamlConfiguration.getString is null
                message.setMessage(StringUtil.translateColorCodes(message.getDefaultMessage()));
                continue;
            }
            message.setMessage(StringUtil.translateColorCodes(this.yamlConfiguration.getString("messages." + message.name().toLowerCase())));
        }
        // Leaderboard
        for (Leaderboard leaderboard : Leaderboard.values()) {
            if (!(this.file.exists()) || this.yamlConfiguration.getString("leaderboard." + leaderboard.name().toLowerCase()) == null) {
                this.yamlConfiguration.set("leaderboard." + leaderboard.name().toLowerCase(), leaderboard.getDefaultValue().toString());
                hasFileChanges = true;
                // set Message's message to his default message and skip the next line, because by new mess yamlConfiguration.getString is null
                leaderboard.setValue(StringUtil.translateColorCodes(leaderboard.getDefaultValue().toString()));
                continue;
            }
            leaderboard.setValue(StringUtil.translateColorCodes(this.yamlConfiguration.getString("leaderboard." + leaderboard.name().toLowerCase())));
        }
        if (hasFileChanges) {
            FileUtil.saveFile(this.yamlConfiguration, this.file);
        }
    }

    private void checkFileExists() {
        // Rename old config (update from version < 2.1)
        if (this.file.exists() // check if file exists without 2 random messages with the new path
                && this.yamlConfiguration.getString("messages.get_money") == null
                && this.yamlConfiguration.getString("messages.eco_take") == null) {
            File oldFile = new File("plugins/" + this.plugin.getName() + "/configOld.yml");
            this.file.renameTo(oldFile);

            this.file = new File("plugins/" + this.plugin.getName() + "/config.yml");
        }
    }
}
