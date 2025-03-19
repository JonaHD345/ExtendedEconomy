package de.jonahd345.extendedeconomy;

import de.jonahd345.extendedeconomy.command.*;
import de.jonahd345.extendedeconomy.config.Config;
import de.jonahd345.extendedeconomy.config.MySql;
import de.jonahd345.extendedeconomy.listener.ConnectionListener;
import de.jonahd345.extendedeconomy.manager.ExpansionManager;
import de.jonahd345.extendedeconomy.provider.DatabaseProvider;
import de.jonahd345.extendedeconomy.provider.EconomyProvider;
import de.jonahd345.extendedeconomy.service.ConfigService;
import de.jonahd345.extendedeconomy.service.EconomyService;
import de.jonahd345.extendedeconomy.service.UpdateService;
import de.jonahd345.extendedeconomy.util.FileUtil;
import de.jonahd345.extendedeconomy.util.LogFilter;
import de.jonahd345.extendedeconomy.util.Metrics;
import de.jonahd345.extendedeconomy.util.TopPlayerSerializer;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

@Getter
public final class ExtendedEconomy extends JavaPlugin {
    private Metrics metrics;

    private ExpansionManager expansionManager;

    private Economy economy;

    private UpdateService updateService;

    private ConfigService configService;

    private DatabaseProvider databaseProvider;

    private EconomyService economyService;

    private TopPlayerSerializer topPlayerSerializer;

    @Override
    public void onEnable() {
        this.metrics = new Metrics(this, 22975);

        LogFilter.registerFilter();

        if (!(setupEconomy())) {
            getLogger().info("No Vault was found! PLUGIN DISABLED!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!(setupPlaceholderAPI())) {
            getLogger().info("No PlaceholderAPI was found!");
        }

        // Rename the old directory to the new one (EasyEconomy -> ExtendedEconomy)
        File directory = new File("plugins/EasyEconomy");
        if (directory.exists() && directory.isDirectory()) {
            File newDirectory = new File(directory.getParent() + File.separator + this.getName());

            if (!directory.renameTo(newDirectory)) {
                getLogger().info("ExtendedEconomy - ERROR MESSAGE TO THE PLUGIN CREATOR!!");
            }
        }

        this.updateService = new UpdateService(this);

        this.configService = new ConfigService(this);
        this.configService.loadConfig();

        if (Config.MYSQL.getValueAsBoolean()) {
            this.databaseProvider = new DatabaseProvider(MySql.HOST.getValue(), MySql.PORT.getValue(), MySql.USER.getValue(), MySql.PASSWORD.getValue(), MySql.DATABASE.getValue(), getLogger(), getDescription().getVersion());
            if (this.databaseProvider.isTablePresent("easyeconomy_coins")) {
                this.databaseProvider.update("RENAME TABLE easyeconomy_coins TO extendedeconomy_coins;"); // Rename table from EasyEconomy to ExtendedEconomy
            }
        } else {
            FileUtil.createDirectory(new File("plugins/" + this.getName() + "/coins"));
            this.databaseProvider = new DatabaseProvider("plugins/" + this.getName() + "/coins/coins.db");
        }
        this.databaseProvider.update("CREATE TABLE IF NOT EXISTS extendedeconomy_coins(uuid VARCHAR(128) PRIMARY KEY, coins VARCHAR(128));");

        this.topPlayerSerializer = new TopPlayerSerializer();

        this.economyService = new EconomyService(this);
        this.economyService.loadTopPlayers();


        this.init();

        // Keep TopPlayers up to date (every 5min)
        new BukkitRunnable() {
            @Override
            public void run() {
                getEconomyService().refreshTopPlayers();
            }
        }.runTaskTimerAsynchronously(this, 6000L, 6000L);

        // Push EconomyPlayers to the database (every 5min)
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(p -> getEconomyService().updateEconomyPlayer(p.getUniqueId()));
            }
        }.runTaskTimerAsynchronously(this, 6000L, 6000L);

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> this.economyService.updateEconomyPlayer(player.getUniqueId()));
        this.economyService.pushTopPlayers();
        this.databaseProvider.disconnect();
    }

    private void init() {
        PluginManager pluginManager = getServer().getPluginManager();

        // Events
        pluginManager.registerEvents(new ConnectionListener(this), this);

        // Commands
        getCommand("extendedeconomy").setExecutor(new ExtendedEconomyCommand(this));
        getCommand("economy").setExecutor(new EconomyCommand(this));
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("balancetop").setExecutor(new BalanceTopCommand(this));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        getServer().getServicesManager().register(Economy.class, new EconomyProvider(this),this, ServicePriority.Highest);
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = rsp.getProvider();
        return true;
    }

    private boolean setupPlaceholderAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return false;
        }
        this.expansionManager = new ExpansionManager(this);
        this.expansionManager.register();
        return true;
    }

    public static ExtendedEconomy getInstance() {
        return getPlugin(ExtendedEconomy.class);
    }
}
