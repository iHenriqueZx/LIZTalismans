package br.com.henriplugins;

import br.com.henriplugins.commands.TalismanCommand;
import br.com.henriplugins.config.ConfigManager;
import br.com.henriplugins.listener.TalismanBagListener;
import br.com.henriplugins.listener.TalismanListener;
import br.com.henriplugins.objects.TalismanBag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class LIZTalismans extends JavaPlugin {

    private static LIZTalismans instance;
    private ConfigManager configManager;
    private TalismanBag talismanBag;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        talismanBag = new TalismanBag(this);

        Bukkit.getPluginManager().registerEvents(new TalismanListener(this), this);
        getServer().getPluginManager().registerEvents(new TalismanBagListener(this), this);
        getCommand("liztalismans").setExecutor(new TalismanCommand(this));

        if (configManager.isBagEnabled()) registerBagRecipe();
    }

    @Override
    public void onDisable() {

    }
    private void registerBagRecipe() {
        ConfigurationSection recipeSection = configManager.getBagRecipe();
        if (recipeSection == null || !recipeSection.getBoolean("enabled", false)) return;

        NamespacedKey key = new NamespacedKey(this, "talisman_bag");
        ItemStack result = talismanBag.createBagItem();
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape(recipeSection.getStringList("shape").toArray(new String[0]));
        ConfigurationSection ingredients = recipeSection.getConfigurationSection("ingredients");
        if (ingredients != null) {
            for (String ch : ingredients.getKeys(false)) {
                Material mat = Material.matchMaterial(ingredients.getString(ch));
                if (mat != null) recipe.setIngredient(ch.charAt(0), mat);
            }
        }

        Bukkit.addRecipe(recipe);
    }
    public static LIZTalismans getInstance() {
        return instance;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
    public TalismanBag getTalismanBag() {
        return talismanBag;
    }
}
