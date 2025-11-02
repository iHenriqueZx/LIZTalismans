package br.com.henriplugins.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final String material;
    private String name;
    private List<String> lore;

    public ItemBuilder(String material) {
        this.material = material;
    }

    public ItemBuilder setName(String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore.stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                .collect(Collectors.toList());
        return this;
    }

    public ItemStack build() {
        ItemStack item;

        if (material.contains(":") && isItemsAdderEnabled()) {
            ItemStack iaItem = getItemsAdderItem(material);
            if (iaItem != null) {
                item = iaItem;
            } else {
                item = new ItemStack(Material.STONE);
                Bukkit.getLogger().warning("[LIZTalismans] Não foi possível encontrar o item do ItemsAdder: " + material);
            }
        } else if (material.length() > 50) {
            item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta != null) {
                try {
                    PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "TalismanBag");
                    PlayerTextures textures = profile.getTextures();
                    textures.setSkin(new URL("http://textures.minecraft.net/texture/" + material));
                    profile.setTextures(textures);
                    meta.setOwnerProfile(profile);

                    if (name != null) meta.setDisplayName(name);
                    if (lore != null) meta.setLore(lore);

                    item.setItemMeta(meta);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Material mat = Material.matchMaterial(material);
            if (mat == null) mat = Material.STONE;
            item = new ItemStack(mat);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null) meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private boolean isItemsAdderEnabled() {
        return Bukkit.getPluginManager().getPlugin("ItemsAdder") != null;
    }

    private ItemStack getItemsAdderItem(String id) {
        try {
            Class<?> clazz = Class.forName("dev.lone.itemsadder.api.CustomStack");
            Object custom = clazz.getMethod("getInstance", String.class).invoke(null, id);
            if (custom != null) {
                return (ItemStack) clazz.getMethod("getItemStack").invoke(custom);
            }
        } catch (Exception ignored) {}
        return null;
    }
}
