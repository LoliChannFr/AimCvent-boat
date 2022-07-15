package me.aimcventboat.main.listener;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class clickevent implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        //ClassItem
        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez la course")) {
            e.setCancelled(true);

            try {
                // create a reader
                Reader reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));


                // create parser
                JsonObject parser = (JsonObject) Jsoner.deserialize(reader);

                List<String> runs = (List<String>) parser.get("runslist");

                String nb = String.valueOf(Objects.requireNonNull(e.getCurrentItem()).getItemMeta().getCustomModelData());

                if (!runs.contains(nb)) {

                    player.closeInventory();
                    player.sendMessage("La course que vous avez sélectionnée n'existe pas.");

                    return;
                }

                Inventory gui = Bukkit.createInventory(player, 9, "§8Choisissez l'emplacement");


                List<ItemStack> menu = new ArrayList<>();

                ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                ItemMeta metaglass = glass.getItemMeta();
                metaglass.setDisplayName("§f ");
                metaglass.setCustomModelData((Integer.valueOf(nb)));
                glass.setItemMeta(metaglass);

                menu.add(glass);
                menu.add(glass);

                for (int i = 1; i < 6; i++) {
                    ItemStack wrench = new ItemStack(Material.BLUE_WOOL);
                    ItemMeta metawrench = wrench.getItemMeta();
                    metawrench.setCustomModelData(i);
                    metawrench.setDisplayName("§fPosition #" + i);
                    wrench.setItemMeta(metawrench);

                    menu.add(wrench);

                }

                menu.add(glass);
                menu.add(glass);

                ItemStack[] menu_items = menu.toArray(new ItemStack[0]);

                gui.setContents(menu_items);
                player.openInventory(gui);
            } catch (JsonException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez l'emplacement")) {

            e.setCancelled(true);

            try {
                // create a reader
                Reader reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));


                // create parser
                JsonObject parser = (JsonObject) Jsoner.deserialize(reader);

                String nb = String.valueOf(Objects.requireNonNull(e.getInventory().getContents()[0]).getItemMeta().getCustomModelData());

                String boat = String.valueOf(e.getCurrentItem().getItemMeta().getCustomModelData());

                try {

                    JsonObject selected_run = (JsonObject) parser.get(nb);

                    JsonObject selected_place = (JsonObject) selected_run.get("start_place");

                    List<Double> coord = new ArrayList<>();
                    coord.add(player.getLocation().getX());
                    coord.add(player.getLocation().getY());
                    coord.add(player.getLocation().getZ());

                    Float direction = player.getLocation().getYaw();

                    selected_place.put(boat, coord);
                    selected_run.put("start_direction",String.valueOf(direction));

                    parser.put(nb, selected_run);

                    BufferedWriter writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));

                    Jsoner.serialize(parser, writer);

                    writer.close();
                    reader.close();

                    player.sendMessage("Position de spawn de la course " + nb + " enregistrée !");

                    player.closeInventory();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            } catch (JsonException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez la course à lancer")) {
            e.setCancelled(true);

            try {
                // create a reader
                Reader reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));


                // create parser
                JsonObject parser = (JsonObject) Jsoner.deserialize(reader);

                List<String> runs = (List<String>) parser.get("runslist");

                String nb = String.valueOf(Objects.requireNonNull(e.getCurrentItem()).getItemMeta().getCustomModelData());

                if (!runs.contains(nb)) {

                    player.closeInventory();
                    player.sendMessage("La course que vous avez sélectionnée n'existe pas.");

                    return;
                }

                World world = player.getWorld();
                Location loc = player.getLocation();

                Entity entity = world.spawnEntity(loc, EntityType.VILLAGER);
                Villager villager = (Villager) entity;
                villager.setProfession(Villager.Profession.ARMORER);
                villager.setVillagerType(Villager.Type.PLAINS);
                villager.setAI(false);
                villager.setCustomName("Cliquez pour rejoindre la course !");
                villager.setCustomNameVisible(true);
                villager.setInvulnerable(true);
                villager.setGravity(false);
                villager.setVillagerLevel(Integer.valueOf(nb));
                villager.setPersistent(true);
                player.closeInventory();

                List<Double> coord = new ArrayList<>();
                coord.add(loc.getX());
                coord.add(loc.getY());
                coord.add(loc.getZ());

                JsonObject selected_run = (JsonObject) parser.get(nb);
                selected_run.put("backup-place",coord);

                BufferedWriter writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));

                Jsoner.serialize(parser, writer);

                writer.close();
                reader.close();

            } catch (JsonException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
