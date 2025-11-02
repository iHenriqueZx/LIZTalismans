package br.com.henriplugins.listener;

import br.com.henriplugins.LIZTalismans;
import br.com.henriplugins.config.ConfigManager;
import br.com.henriplugins.model.Talisman;
import br.com.henriplugins.objects.TalismanBag;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Base64;
import java.util.List;

public class TalismanListener implements Listener {

    private final LIZTalismans plugin;
    private final ConfigManager configManager;
    private final TalismanBag bagManager;
    private final NamespacedKey bagNamesKey;

    public TalismanListener(LIZTalismans plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.bagManager = plugin.getTalismanBag();
        this.bagNamesKey = new NamespacedKey(plugin, "talisman_bag_names");
        startEffectTask();
    }

    private void startEffectTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                applyTalismans(player);
            }
        }, 20L, 20L);
    }

    private void applyTalismans(Player player) {
        boolean hotbarMode = configManager.getFuncionamento().equalsIgnoreCase("hotbar");
        boolean bagEnabled = configManager.isBagEnabled();

        for (Talisman talisman : configManager.getTalismans().values()) {
            if (hasTalismanInInventory(player, talisman, hotbarMode)) {
                applyEffect(player, talisman);
            }
        }

        if (bagEnabled) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || !bagManager.isBag(item)) continue;

                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                PersistentDataContainer data = meta.getPersistentDataContainer();
                if (!data.has(bagNamesKey, PersistentDataType.STRING)) continue;

                List<String> nomes = deserializeStringList(data.get(bagNamesKey, PersistentDataType.STRING));
                if (nomes == null || nomes.isEmpty()) continue;

                for (String nomeTalisman : nomes) {
                    for (Talisman talisman : configManager.getTalismans().values()) {
                        String configNome = talisman.getItem().getItemMeta() != null ?
                                talisman.getItem().getItemMeta().getDisplayName() : "";
                        if (configNome.equalsIgnoreCase(nomeTalisman)) {
                            applyEffect(player, talisman);
                        }
                    }
                }
            }
        }
    }

    private boolean hasTalismanInInventory(Player player, Talisman talisman, boolean hotbarOnly) {
        if (hotbarOnly) {
            for (int i = 0; i < 9; i++) {
                ItemStack slot = player.getInventory().getItem(i);
                if (slot != null && slot.isSimilar(talisman.getItem())) return true;
            }
            return false;
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(talisman.getItem())) return true;
        }
        return false;
    }

    private void applyEffect(Player player, Talisman talisman) {
        PotionEffectType type = talisman.getEffect();
        if (type == null) return;

        PotionEffect current = player.getPotionEffect(type);
        int newAmplifier = talisman.getAmplifier();

        if (current == null || current.getAmplifier() < newAmplifier) {
            player.addPotionEffect(new PotionEffect(type, 100, newAmplifier, true, false, true));
        }
    }

    private List<String> deserializeStringList(String base64) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64);
            try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(decoded);
                 java.io.ObjectInputStream ois = new java.io.ObjectInputStream(in)) {
                int size = ois.readInt();
                List<String> list = new java.util.ArrayList<>();
                for (int i = 0; i < size; i++) {
                    list.add((String) ois.readObject());
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
