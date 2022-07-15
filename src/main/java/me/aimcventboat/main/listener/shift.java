package me.aimcventboat.main.listener;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class shift implements Listener {

    @EventHandler
    public void onVehicleExit(VehicleExitEvent e) {

        Vehicle vehicle = e.getVehicle();
        LivingEntity player = e.getExited();

        if (!player.getType().equals(EntityType.PLAYER)) {

            return;
        }
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));

            // create parser
            JsonObject parser = (JsonObject) Jsoner.deserialize(reader);

            JsonObject playerlist = (JsonObject) parser.get("playerlist");

            if (!playerlist.containsKey(player.getName())) {
                return;
            }


            JsonObject run = (JsonObject) parser.get(String.valueOf(((JsonObject)playerlist.get(player.getName())).get("race")));

            player.sendMessage(String.valueOf(run));
            player.sendMessage(String.valueOf(playerlist.get(player.getName())));

            ((List<String>)run.get("players")).remove(player.getName());

            playerlist.remove(player.getName());

            List<Double> coord = (List<Double>) run.get("backup-place");

            Location loc = player.getLocation();
            loc.setX(Float.valueOf(String.valueOf(coord.get(0)))); loc.setY(Float.valueOf(String.valueOf(coord.get(1)))); loc.setZ(Float.valueOf(String.valueOf(coord.get(2))));


            vehicle.remove();

            BufferedWriter writer = null;

            try {
                writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));
                Jsoner.serialize(parser, writer);
                writer.close();
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            player.teleport(loc);

            player.sendMessage("Vous avez été retiré de la course.");

            return;

        } catch (IOException | JsonException ex) {
            ex.printStackTrace();
        }



    }

}
