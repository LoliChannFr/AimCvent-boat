package me.aimcventboat.main.commands;

import me.aimcventboat.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import static me.aimcventboat.main.fonction.ConfigGet;

public class Test implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player){

            Player player = (Player) sender;

            ItemStack item = new ItemStack(Material.ACACIA_PRESSURE_PLATE);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(69);
            item.setItemMeta(meta);

            player.getInventory().addItem(item);

            FileConfiguration cfg = ConfigGet("test");

            player.sendMessage(cfg.getString("test"));

            player.sendMessage("Test");

        }

        return true;
    }





}
