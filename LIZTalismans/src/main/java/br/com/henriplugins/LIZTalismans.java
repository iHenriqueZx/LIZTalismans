package br.com.henriplugins;

import br.com.henriplugins.commands.TalismanCommand;
import br.com.henriplugins.config.ConfigManager;
import br.com.henriplugins.listener.TalismanListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LIZTalismans extends JavaPlugin {

    private static LIZTalismans instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        this.configManager = new ConfigManager(this);

        Bukkit.getPluginManager().registerEvents(new TalismanListener(configManager), this);
        getCommand("liztalismans").setExecutor(new TalismanCommand(this));

    }

    @Override
    public void onDisable() {

    }

    public static LIZTalismans getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
