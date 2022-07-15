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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class villageropen implements Listener {

    @EventHandler
    public void onVillager(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();

        if (entity.getType().equals(EntityType.VILLAGER)) {
            if (entity.getName().equals("Cliquez pour rejoindre la course !")) {
                e.setCancelled(true);
                
                Villager villager = (Villager) entity;
                String nb = String.valueOf(villager.getVillagerLevel());

                // create a reader
                Reader reader = null;
                try {
                    reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // create parser
                JsonObject parser = null;
                try {
                    parser = (JsonObject) Jsoner.deserialize(reader);
                } catch (JsonException ex) {
                    ex.printStackTrace();
                }

                List<String> runs = (List<String>) parser.get("runslist");

                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if (runs.contains(nb)) {

                    player.sendMessage("test");

                    JsonObject run = (JsonObject) parser.get(nb);
                    JsonObject start_place = (JsonObject) run.get("start_place");

                    Integer place = ((List<String>)run.get("players")).size() + 1;

                    if (place > 5){
                        player.sendMessage("La course est pleine.");
                        return;
                    }

                    player.sendMessage(String.valueOf(place));

                    List<Double> coord = (List<Double>) start_place.get(String.valueOf(place));

                    player.sendMessage(String.valueOf(coord));
                    player.sendMessage(String.valueOf(start_place));

                    Location loc = player.getLocation();
                    loc.setX(Float.valueOf(String.valueOf(coord.get(0)))); loc.setY(Float.valueOf(String.valueOf(coord.get(1)))); loc.setZ(Float.valueOf(String.valueOf(coord.get(2))));
                    loc.setYaw(Float.valueOf((String) run.get("start_direction")));

                    player.teleport(loc);

                    World world = player.getWorld();

                    Entity boat = world.spawnEntity(loc, EntityType.BOAT);

                    boat.addPassenger(player);

                    BufferedWriter writer = null;

                    JsonObject playerinfo = new JsonObject();

                    long currenttime = Instant.now().getEpochSecond();

                    player.sendMessage(String.valueOf(currenttime - System.currentTimeMillis()/1000));

                    playerinfo.put("race",nb);
                    playerinfo.put("time",currenttime);
                    playerinfo.put("name",player.getName());

                    player.sendMessage(String.valueOf(System.currentTimeMillis()));


                    ((JsonObject)parser.get("playerlist")).put(player.getName(), playerinfo);
                    ((List<String>)run.get("players")).add(player.getName());

                    HashMap<String, Integer> pl = new HashMap<>();
                    pl.put(player.getName(), Integer.valueOf(nb));
                    player.sendMessage(String.valueOf(pl));

                    try {
                        writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));
                        Jsoner.serialize(parser, writer);
                        writer.close();
                        reader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                }




            
            }
        }
    }
}
