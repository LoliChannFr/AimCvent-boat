package me.aimcventboat.main.commands;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.fonction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class compet_join implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        Inventory gui = Bukkit.createInventory(player, 54, "§8Choisissez la compétition");

            FileConfiguration data = fonction.ConfigGet("./runs/data");


            List<String> runs = (List<String>) data.get("runslist");

            List<ItemStack> menu = new ArrayList<>();


            for (String i : runs) {

                FileConfiguration run = fonction.ConfigGet("./runs/" + i);

                ItemStack wrench = new ItemStack(Material.BLUE_WOOL);

                if ((Boolean) run.get("compet")) {
                    wrench.setType(Material.GREEN_WOOL);
                }
                else {
                    wrench.setType(Material.BLUE_WOOL);
                }

                ItemMeta metawrench = wrench.getItemMeta();
                metawrench.setCustomModelData(Integer.valueOf(i));
                metawrench.setDisplayName("§fCourse #" + i);
                wrench.setItemMeta(metawrench);

                menu.add(wrench);

            }

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setDisplayName(args[0]);
            meta.setOwner(args[0]);
            head.setItemMeta(meta);

            for (int i = 1; i < 54 - runs.size(); i++){
                ItemStack item = new ItemStack(Material.AIR);

                menu.add(item);
            }

            menu.add(head);

            ItemStack[] menu_items = menu.toArray(new ItemStack[0]);

            gui.setContents(menu_items);
            player.openInventory(gui);



        return false;
    }
}
