package me.aimcventboat.main.listener;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

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

                try {

                    JsonObject selected_run = (JsonObject) parser.get(nb);

                    List<Double> coord = new ArrayList<>();
                    coord.add(player.getLocation().getX());
                    coord.add(player.getLocation().getY());
                    coord.add(player.getLocation().getZ());

                    Float direction = player.getLocation().getYaw();

                    selected_run.put("start_place",coord);
                    selected_run.put("start_direction",direction);

                    parser.put(nb, selected_run);

                    BufferedWriter writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));

                    Jsoner.serialize(parser, writer);

                    writer.close();
                    reader.close();

                    player.sendMessage("Position de spawn de la course " + nb + "enregistrée !");

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
    }
}