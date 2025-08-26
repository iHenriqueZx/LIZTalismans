package br.com.henriplugins.model;

import br.com.henriplugins.util.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Talisman {

    private final String name;
    private final String material;
    private final List<String> lore;
    private final PotionEffectType effect;
    private final int amplifier;
    private final ItemStack item;

    public Talisman(ConfigurationSection section) {
        this.name = section.getString("nome", "Talisman");
        this.material = section.getString("material", "STONE");
        this.lore = section.getStringList("lore");

        String efeitoRaw = section.getString("efeito", "SPEED;1");
        String[] split = efeitoRaw.split(";");
        PotionEffectType type = PotionEffectType.getByName(split[0].toUpperCase());
        int amp = 0;
        if (split.length > 1) {
            try {
                amp = Integer.parseInt(split[1]) - 1;
            } catch (NumberFormatException ignored) {}
        }
        this.effect = type;
        this.amplifier = amp;

        this.item = new ItemBuilder(material)
                .setName(name)
                .setLore(lore)
                .build();
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public PotionEffectType getEffect() {
        return effect;
    }

    public int getAmplifier() {
        return amplifier;
    }
}
