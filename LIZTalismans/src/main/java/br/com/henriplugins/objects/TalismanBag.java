package br.com.henriplugins.objects;

import br.com.henriplugins.LIZTalismans;
import br.com.henriplugins.config.ConfigManager;
import br.com.henriplugins.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class TalismanBag {

    private final LIZTalismans plugin;

    public TalismanBag(LIZTalismans plugin) {
        this.plugin = plugin;
    }

    public ItemStack createBagItem() {
        ConfigManager cfg = plugin.getConfigManager();

        ItemBuilder builder = new ItemBuilder(cfg.getBagMaterial())
                .setName(cfg.getBagName())
                .setLore(cfg.getBagLore());

        return builder.build();
    }

    public boolean isBag(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String expected = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getBagName()));
        String display = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        return display.equalsIgnoreCase(expected);
    }
}

