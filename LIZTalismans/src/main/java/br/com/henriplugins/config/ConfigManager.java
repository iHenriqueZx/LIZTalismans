package br.com.henriplugins.config;

import br.com.henriplugins.LIZTalismans;
import br.com.henriplugins.model.Talisman;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final LIZTalismans plugin;
    private final Map<String, Talisman> talismans = new HashMap<>();
    private String funcionamento;

    public ConfigManager(LIZTalismans plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.reloadConfig();
        funcionamento = plugin.getConfig().getString("funcionamento", "hotbar").toLowerCase();

        talismans.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("talismans");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                talismans.put(key, new Talisman(section.getConfigurationSection(key)));
            }
        }
    }

    public String getFuncionamento() {
        return funcionamento;
    }

    public Map<String, Talisman> getTalismans() {
        return talismans;
    }
}
