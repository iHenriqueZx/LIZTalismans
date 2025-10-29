package br.com.henriplugins.config;

import br.com.henriplugins.LIZTalismans;
import br.com.henriplugins.model.Talisman;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigManager {

    private final LIZTalismans plugin;
    private final Map<String, Talisman> talismans = new HashMap<>();
    private String funcionamento;
    private boolean bagEnabled;
    private String bagName;
    private String bagMaterial;
    private List<String> bagLore;
    private int bagSlots;
    private ConfigurationSection bagRecipe;

    public ConfigManager(LIZTalismans plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        funcionamento = plugin.getConfig().getString("funcionamento", "hotbar").toLowerCase();

        talismans.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("talismans");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                talismans.put(key, new Talisman(section.getConfigurationSection(key)));
            }
        }

        ConfigurationSection bagSection = config.getConfigurationSection("talismans_bag");
        if (bagSection != null) {
            this.bagEnabled = bagSection.getBoolean("enable", false);
            this.bagName = bagSection.getString("name", "&f&lTalisman Bag");
            this.bagMaterial = bagSection.getString("material", "CHEST");
            this.bagLore = bagSection.getStringList("lore").stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
            this.bagSlots = bagSection.getInt("slots", 27);
            this.bagRecipe = bagSection.getConfigurationSection("recipe");
        }
    }

    public String getFuncionamento() {
        return funcionamento;
    }
    public Map<String, Talisman> getTalismans() {
        return talismans;
    }
    public boolean isBagEnabled() {
        return bagEnabled;
    }
    public String getBagName() {
        return bagName;
    }
    public String getBagMaterial() {
        return bagMaterial;
    }
    public List<String> getBagLore() {
        return bagLore;
    }
    public int getBagSlots() {
        return bagSlots;
    }
    public ConfigurationSection getBagRecipe() {
        return bagRecipe;
        }
}
