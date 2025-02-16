package de.jonahd345.extendedeconomy.listener;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.config.Config;
import de.jonahd345.extendedeconomy.config.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    private ExtendedEconomy plugin;

    public ConnectionListener(ExtendedEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getDatabaseProvider().checkDatabase();
        this.plugin.getEconomyService().loadEconomyPlayer(player.getUniqueId());
        if (this.plugin.getUpdateService().isUpdateAvailable() && Config.UPDATE_NOTIFICATION.getValueAsBoolean()) {
            if (player.hasPermission("extendedeconomy.admin")) {
                player.sendMessage(Message.PREFIX.getMessage() + "§7The new Version from §a§lExtendedEconomy §7v§a" +
                        this.plugin.getUpdateService().getSpigotVersion().replace(".", "§7.§a") +
                        " §7is available at§8: §2https://www.spigotmc.org/resources/extendedeconomy.106888/");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.plugin.getEconomyService().pushEconomyPlayer(player.getUniqueId());
    }
}
