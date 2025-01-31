package de.jonahd345.extendedeconomy.provider;

import de.jonahd345.extendedeconomy.ExtendedEconomy;
import de.jonahd345.extendedeconomy.model.EconomyPlayer;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.List;

public class EconomyProvider implements Economy {
    private ExtendedEconomy plugin;

    public EconomyProvider(ExtendedEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return this.plugin.isEnabled();
    }

    @Override
    public String getName() {
        return "ExtendedEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double v) {
        return null;
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public boolean hasAccount(String s) {
        return this.plugin.getEconomyPlayer().containsKey(Bukkit.getOfflinePlayer(s).getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return this.plugin.getEconomyPlayer().containsKey(offlinePlayer.getUniqueId());
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return this.hasAccount(s);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return this.hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String s) {
        return this.plugin.getEconomyPlayer().get(Bukkit.getOfflinePlayer(s).getUniqueId()).getCoins();
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return this.plugin.getEconomyPlayer().get(offlinePlayer.getUniqueId()).getCoins();
    }

    @Override
    public double getBalance(String s, String s1) {
        return this.getBalance(s);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return this.getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String s, double v) {
        return this.plugin.getEconomyPlayer().get(Bukkit.getOfflinePlayer(s).getUniqueId()).getCoins() <= v;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return this.plugin.getEconomyPlayer().get(offlinePlayer.getUniqueId()).getCoins() <= v;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return this.has(s, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return this.has(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        this.plugin.getEconomyPlayer().get(Bukkit.getOfflinePlayer(s).getUniqueId()).setCoins(this.getBalance(Bukkit.getOfflinePlayer(s)) - v);
        return new EconomyResponse(v, this.getBalance(s), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        this.plugin.getEconomyPlayer().get(offlinePlayer.getUniqueId()).setCoins(this.getBalance(offlinePlayer) - v);
        return new EconomyResponse(v, this.getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        this.plugin.getEconomyPlayer().get(Bukkit.getOfflinePlayer(s).getUniqueId()).setCoins(this.getBalance(Bukkit.getOfflinePlayer(s)) - v);
        return new EconomyResponse(v, this.getBalance(s), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        this.plugin.getEconomyPlayer().get(offlinePlayer.getUniqueId()).setCoins(this.getBalance(offlinePlayer) - v);
        return new EconomyResponse(v, this.getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        this.plugin.getEconomyPlayer().get(Bukkit.getOfflinePlayer(s).getUniqueId()).setCoins(this.getBalance(Bukkit.getOfflinePlayer(s)) + v);
        return new EconomyResponse(v, this.getBalance(s), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        this.plugin.getEconomyPlayer().get(offlinePlayer.getUniqueId()).setCoins((this.getBalance(offlinePlayer) + v));
        return new EconomyResponse(v, this.getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        this.plugin.getEconomyPlayer().get(Bukkit.getOfflinePlayer(s).getUniqueId()).setCoins(this.getBalance(Bukkit.getOfflinePlayer(s)) + v);
        return new EconomyResponse(v, this.getBalance(s), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        this.plugin.getEconomyPlayer().get(offlinePlayer.getUniqueId()).setCoins(this.getBalance(offlinePlayer) + v);
        return new EconomyResponse(v, this.getBalance(offlinePlayer), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, this.plugin.getConfigService().getMessages().get("prefix") +
                "ExtendedEconomy doesn't support bank accounts!");    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        if (this.plugin.getEconomyPlayer().containsKey(Bukkit.getOfflinePlayer(s).getUniqueId())) {
            return false;
        }
        this.plugin.getEconomyPlayer().put(Bukkit.getOfflinePlayer(s).getUniqueId(), new EconomyPlayer(Bukkit.getOfflinePlayer(s).getUniqueId()));
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        if (this.plugin.getEconomyPlayer().containsKey(offlinePlayer.getUniqueId())) {
            return false;
        }
        this.plugin.getEconomyPlayer().put(offlinePlayer.getUniqueId(), new EconomyPlayer(offlinePlayer.getUniqueId()));
        return true;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        if (this.plugin.getEconomyPlayer().containsKey(Bukkit.getOfflinePlayer(s).getUniqueId())) {
            return false;
        }
        this.plugin.getEconomyPlayer().put(Bukkit.getOfflinePlayer(s).getUniqueId(), new EconomyPlayer(Bukkit.getOfflinePlayer(s).getUniqueId()));
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        if (this.plugin.getEconomyPlayer().containsKey(offlinePlayer.getUniqueId())) {
            return false;
        }
        this.plugin.getEconomyPlayer().put(offlinePlayer.getUniqueId(), new EconomyPlayer(offlinePlayer.getUniqueId()));
        return true;
    }
}
