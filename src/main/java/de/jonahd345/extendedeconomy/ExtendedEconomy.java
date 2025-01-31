package de.jonahd345.extendedeconomy;

import de.jonahd345.extendedeconomy.command.*;
import de.jonahd345.extendedeconomy.listener.ConnectionListener;
import de.jonahd345.extendedeconomy.manager.ExpansionManager;
import de.jonahd345.extendedeconomy.model.EconomyPlayer;
import de.jonahd345.extendedeconomy.model.EconomyTopPlayer;
import de.jonahd345.extendedeconomy.provider.DatabaseProvider;
import de.jonahd345.extendedeconomy.provider.EconomyProvider;
import de.jonahd345.extendedeconomy.service.CacheService;
import de.jonahd345.extendedeconomy.service.EconomyService;
import de.jonahd345.extendedeconomy.service.UpdateService;
import de.jonahd345.extendedeconomy.util.Metrics;
import de.jonahd345.extendedeconomy.util.Number;
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
import java.util.*;

public final class ExtendedEconomy extends JavaPlugin {
    private Metrics metrics;
    private ExpansionManager expansionManager;

    @Getter
    private Economy economy;
    @Getter
    private UpdateService updateService;
    @Getter
    private Map<UUID, EconomyPlayer> economyPlayer;
    @Getter
    private List<EconomyTopPlayer> economyTopPlayer;
    @Getter
    private CacheService cacheService;
    @Getter
    private DatabaseProvider databaseProvider;
    @Getter
    private EconomyService economyService;
    @Getter
    private Number number;
    @Getter
    private TopPlayerSerializer topPlayerSerializer;

    @Override
    public void onEnable() {
        this.metrics = new Metrics(this, 22975);

        if (!(setupEconomy())) {
            getLogger().info("No Vault was found! PLUGIN DISABLED!");
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

        this.economyPlayer = new HashMap<>();
        this.economyTopPlayer = new ArrayList<>();

        this.cacheService = new CacheService(this);
        this.cacheService.loadCache();

        if (CacheService.MYSQL) {
            this.databaseProvider = new DatabaseProvider(this.cacheService.getMessages().get("mysql.host"), this.cacheService.getMessages().get("mysql.port"),
                    this.cacheService.getMessages().get("mysql.user"), this.cacheService.getMessages().get("mysql.password"), this.cacheService.getMessages().get("mysql.database"));
            this.databaseProvider.update("RENAME TABLE IF EXISTS easyeconomy_coins TO extendedeconomy_coins;");
            this.databaseProvider.update("CREATE TABLE IF NOT EXISTS extendedeconomy_coins(uuid VARCHAR(128), coins VARCHAR(128));");
        }

        this.topPlayerSerializer = new TopPlayerSerializer();

        this.economyService = new EconomyService(this);
        this.economyService.setupTopPlayers();

        this.number = new Number();

        this.init();

        // Keep TopPlayers up to date (every 30s)
        new BukkitRunnable() {
            @Override
            public void run() {
                getEconomyService().loadTopPlayers();
            }
        }.runTaskTimerAsynchronously(this, 600L, 600L);

        // Push EconomyPlayers to the database (every 5min)
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(p -> getEconomyService().pushEconomyPlayer(p.getUniqueId()));
            }
        }.runTaskTimerAsynchronously(this, 6000L, 6000L);

        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> this.economyService.pushEconomyPlayer(player.getUniqueId()));
        this.economyService.pushTopPlayers();
        if (CacheService.MYSQL) {
            this.databaseProvider.disconnect();
        }
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
