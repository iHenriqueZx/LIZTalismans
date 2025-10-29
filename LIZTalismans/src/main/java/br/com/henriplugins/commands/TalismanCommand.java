package br.com.henriplugins.commands;

import br.com.henriplugins.LIZTalismans;
import br.com.henriplugins.model.Talisman;
import br.com.henriplugins.objects.TalismanBag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TalismanCommand implements CommandExecutor {

    private final LIZTalismans plugin;

    public TalismanCommand(LIZTalismans plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Use: /" + label + " <reload|give|givebag>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("liztalismans.admin")) {
                sender.sendMessage(ChatColor.RED + "Você não tem permissão.");
                return true;
            }

            plugin.getConfigManager().loadConfig();
            sender.sendMessage(ChatColor.GREEN + "Configuração do LIZTalismans recarregada!");
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("liztalismans.admin")) {
                sender.sendMessage(ChatColor.RED + "Você não tem permissão.");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Use: /" + label + " give <jogador> <talisman>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Jogador não encontrado.");
                return true;
            }

            Talisman talisman = plugin.getConfigManager().getTalismans().get(args[2]);
            if (talisman == null) {
                sender.sendMessage(ChatColor.RED + "Talismã '" + args[2] + "' não existe.");
                return true;
            }

            ItemStack item = talisman.getItem();
            target.getInventory().addItem(item);

            sender.sendMessage(ChatColor.GREEN + "Você deu o talismã '" + args[2] + "' para " + target.getName());
            target.sendMessage(ChatColor.YELLOW + "Você recebeu o talismã " + item.getItemMeta().getDisplayName());

            return true;
        }

        if (args[0].equalsIgnoreCase("givebag")) {
            if (!sender.hasPermission("liztalismans.admin")) {
                sender.sendMessage(ChatColor.RED + "Você não tem permissão.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Use: /" + label + " givebag <jogador>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Jogador não encontrado.");
                return true;
            }

            if (!plugin.getConfigManager().isBagEnabled()) {
                sender.sendMessage(ChatColor.RED + "A bolsa de talismãs não está habilitada na configuração.");
                return true;
            }

            TalismanBag bag = plugin.getTalismanBag();
            ItemStack bagItem = bag.createBagItem();
            target.getInventory().addItem(bagItem);

            sender.sendMessage(ChatColor.GREEN + "Você deu a Bolsa de Talismãs para " + target.getName());
            target.sendMessage(ChatColor.YELLOW + "Você recebeu a Bolsa de Talismãs!");

            return true;
        }

        sender.sendMessage(ChatColor.RED + "Comando desconhecido.");
        return true;
    }
}
