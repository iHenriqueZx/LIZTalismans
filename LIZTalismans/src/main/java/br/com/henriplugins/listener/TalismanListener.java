package br.com.henriplugins.listener;

import br.com.henriplugins.config.ConfigManager;
import br.com.henriplugins.model.Talisman;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class TalismanListener implements Listener {

    private final ConfigManager configManager;

    public TalismanListener(ConfigManager configManager) {
        this.configManager = configManager;
        startEffectTask();
    }

    private void startEffectTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    applyTalismans(player);
                }
            }
        }.runTaskTimer(br.com.henriplugins.LIZTalismans.getInstance(), 20, 60); // ✅ Thread principal
    }

    private void applyTalismans(Player player) {
        boolean hotbar = configManager.getFuncionamento().equalsIgnoreCase("hotbar");

        configManager.getTalismans().values().forEach(talisman -> {
            boolean hasItem = player.getInventory().containsAtLeast(talisman.getItem(), 1);

            if (hasItem) {
                if (hotbar) {
                    boolean found = false;
                    for (int i = 0; i < 9; i++) {
                        if (player.getInventory().getItem(i) != null &&
                                player.getInventory().getItem(i).isSimilar(talisman.getItem())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) return;
                }

                PotionEffectType type = talisman.getEffect();
                if (type != null) {
                    player.addPotionEffect(
                            new PotionEffect(type, 100, talisman.getAmplifier(), true, false, true)
                    );
                }
            }
        });
    }
}
