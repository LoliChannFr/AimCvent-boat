package me.aimcventboat.main.commands;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;



public class pnjsum implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;
        Location loc = player.getLocation();
        World world = player.getWorld();

        Inventory gui = Bukkit.createInventory(player, 54, "§8Choisissez la course à lancer");

        try {
            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));

            // create parser
            JsonObject parser = (JsonObject) Jsoner.deserialize(reader);

            List<String> runs = (List<String>) parser.get("runslist");

            List<ItemStack> menu = new ArrayList<>();

            for (int i = 1; i < runs.size() + 1; i++) {
                ItemStack wrench = new ItemStack(Material.BLUE_WOOL);
                ItemMeta metawrench = wrench.getItemMeta();
                metawrench.setCustomModelData(i);
                metawrench.setDisplayName("§fCourse #" + i);
                wrench.setItemMeta(metawrench);

                menu.add(wrench);

            }

            ItemStack[] menu_items = menu.toArray(new ItemStack[0]);

            gui.setContents(menu_items);
            player.openInventory(gui);
        } catch (IOException | JsonException e) {
            e.printStackTrace();
        }
    return false;
    }

}
