package br.com.henriplugins.listener;

import br.com.henriplugins.LIZTalismans;
import br.com.henriplugins.config.ConfigManager;
import br.com.henriplugins.model.Talisman;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class TalismanBagListener implements Listener {

    private final LIZTalismans plugin;
    private final ConfigManager config;
    private final NamespacedKey conteudoKey;

    private final Map<Player, Inventory> bolsasAbertas = new HashMap<>();
    private final Map<Player, Integer> bolsaSlotMap = new HashMap<>();
    private final Map<Player, ItemStack> bolsaSalva = new HashMap<>();

    public TalismanBagListener(LIZTalismans plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.conteudoKey = new NamespacedKey(plugin, "talisman_bag_conteudo");
    }

    @EventHandler
    public void aoAbrirBolsa(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return;

        String nomeConfig = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', config.getBagName()));
        String nomeItem = ChatColor.stripColor(meta.getDisplayName());
        if (!nomeItem.equalsIgnoreCase(nomeConfig)) return;

        e.setCancelled(true);

        Inventory bolsa = Bukkit.createInventory(null, config.getBagSlots(),
                ChatColor.translateAlternateColorCodes('&', config.getBagName()));

        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (data.has(conteudoKey, PersistentDataType.BYTE_ARRAY)) {
            try (ByteArrayInputStream in = new ByteArrayInputStream(data.get(conteudoKey, PersistentDataType.BYTE_ARRAY));
                 BukkitObjectInputStream bin = new BukkitObjectInputStream(in)) {
                ItemStack[] conteudo = (ItemStack[]) bin.readObject();
                bolsa.setContents(conteudo);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        p.openInventory(bolsa);
        bolsasAbertas.put(p, bolsa);
        bolsaSlotMap.put(p, p.getInventory().getHeldItemSlot());
        bolsaSalva.put(p, item.clone());
    }

    @EventHandler
    public void aoClicar(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!bolsasAbertas.containsKey(p)) return;

        Inventory inv = e.getClickedInventory();
        if (inv == null) return;

        ItemStack current = e.getCurrentItem();
        ItemStack cursor = e.getCursor();

        if (cursor != null && cursor.getType() != Material.AIR) {
            if (!isTalisman(cursor)) {
                e.setCancelled(true);
                return;
            }
        }

        if (current != null && current.getType() != Material.AIR) {
            if (!isTalisman(current)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void aoFechar(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (!bolsasAbertas.containsKey(p)) return;

        Inventory bolsa = bolsasAbertas.get(p);
        ItemStack itemOriginal = bolsaSalva.get(p);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOut = new BukkitObjectOutputStream(out);
            dataOut.writeObject(bolsa.getContents());
            dataOut.close();

            ItemMeta meta = itemOriginal.getItemMeta();
            meta.getPersistentDataContainer().set(conteudoKey, PersistentDataType.BYTE_ARRAY, out.toByteArray());

            NamespacedKey nomesKey = new NamespacedKey(plugin, "talisman_bag_names");
            List<String> nomes = new ArrayList<>();
            for (ItemStack inside : bolsa.getContents()) {
                if (inside == null || !inside.hasItemMeta()) continue;
                nomes.add(inside.getItemMeta().getDisplayName());
            }
            meta.getPersistentDataContainer().set(nomesKey, PersistentDataType.STRING, serializeStringList(nomes));

            itemOriginal.setItemMeta(meta);

            int slot = bolsaSlotMap.get(p);
            p.getInventory().setItem(slot, itemOriginal);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        bolsasAbertas.remove(p);
        bolsaSlotMap.remove(p);
        bolsaSalva.remove(p);
    }
    private String serializeStringList(List<String> list) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(list.size());
            for (String s : list) dataOutput.writeObject(s);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private boolean isTalisman(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;

        for (Talisman t : config.getTalismans().values()) {
            ItemStack talismanItem = t.getItem();
            if (!talismanItem.hasItemMeta()) continue;

            String nomeItem = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            String nomeTalisman = ChatColor.stripColor(talismanItem.getItemMeta().getDisplayName());

            if (nomeItem.equalsIgnoreCase(nomeTalisman)) return true;
        }

        return false;
    }
}
