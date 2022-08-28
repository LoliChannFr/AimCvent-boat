package me.aimcventboat.main.commands;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.fonction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.aimcventboat.main.fonction.AffichageTime;
import static me.aimcventboat.main.fonction.MemoryToConfig;
import static me.aimcventboat.main.listener.villageropen.Cooldowns;

public class boat_end implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            return true;
        }

        System.out.println(sender.getName());

        System.out.println(args);



        try{
            Player player = Bukkit.getPlayer(args[0]);
            if (sender instanceof BlockCommandSender) {
                Location loc = ((BlockCommandSender) sender).getBlock().getLocation();
                Entity boat = (Entity) loc.getWorld().getNearbyEntities(loc, 4, 4, 4).stream()
                        .filter(e -> e.getType() == EntityType.BOAT)
                        .findFirst()
                        .orElse(null);
                try {
                    if (boat.getPassengers().get(0) instanceof Player) {
                        player = (Player) boat.getPassengers().get(0);
                    }
                } catch (Exception e) {
                    return false;
                }
                player = (Player) Bukkit.getPlayer(boat.getPassengers().get(0).getName());
            }


            FileConfiguration playerlist = fonction.ConfigGet("playerlist");

            if (!playerlist.contains(player.getName())) {
                return true;
            }

            FileConfiguration playerinfo = MemoryToConfig((MemorySection) playerlist.get(player.getName()));

            final long startTimeMillis = Cooldowns.get(player.getName());

            final long endTimeMillis = System.currentTimeMillis();

            final long time = endTimeMillis - startTimeMillis;

            String str = (String) playerinfo.get("race");

            FileConfiguration run = fonction.ConfigGet("./runs/" + str);

            Long lg = null;

            FileConfiguration scores = MemoryToConfig((MemorySection) run.get("scores"));

            try {
                 lg = Long.valueOf(String.valueOf(scores.get(player.getName())));
            } catch (NumberFormatException e) {
               lg = null;
            }

            if ((Boolean) run.get("compet")) {
                if (lg == null || lg > time){

                    (MemoryToConfig((MemorySection) run.get("scores"))).addDefault(player.getName(), time);
                }

                MemorySection scores_compet = (MemorySection) run.get("scores_compet");

                if (scores_compet == null) {
                    scores_compet = (FileConfiguration) new YamlConfiguration();

                } else {
                    scores_compet = MemoryToConfig(scores_compet);
                }

                scores_compet.addDefault(player.getName(), time);

                ((FileConfiguration) scores_compet).options().copyDefaults(true);

                run.set("scores_compet", scores_compet);

                ((List<String>)run.get("players")).remove(player.getName());

                if (((List<String>)run.get("players")).size() == 0) {
                    Bukkit.broadcastMessage("La compétition de la course n°" + str + " est terminée !");
                    Bukkit.broadcastMessage("Les scores sont les suivants :");
                    for (String name : scores_compet.getKeys(false)) {
                        String score = AffichageTime(Long.valueOf(String.valueOf(scores_compet.get(name))));

                        Bukkit.broadcastMessage(name + " | " + score);
                    }
                    Bukkit.broadcastMessage("Bien joué à tous !");

                    run.addDefault("started", false);
                }

                playerlist.set(player.getName(), null);

                Vehicle vehicle = (Vehicle) player.getVehicle();

                try {
                    vehicle.remove();
                } catch (Exception e) {

                }
            }


            else {

                if (lg == null || lg > time) {

                    scores.set(player.getName(), time);

                    run.set("scores", scores);
                }

                ((List<String>) run.get("players")).remove(player.getName());

                playerlist.set(player.getName(), null);

                Vehicle vehicle = (Vehicle) player.getVehicle();

                vehicle.remove();

            }
            List<Double> coord = (List<Double>) run.get("backup-place");
            Location loc = player.getLocation();
            loc.setX(Float.valueOf(String.valueOf(coord.get(0))));
            loc.setY(Float.valueOf(String.valueOf(coord.get(1))));
            loc.setZ(Float.valueOf(String.valueOf(coord.get(2))));

            fonction.ConfigSave(run, "./runs/" + str);
            fonction.ConfigSave(playerlist, "playerlist");

            player.teleport(loc);

            player.sendMessage("Vous avez fini la course en : " + AffichageTime(time) + " !");

        } catch (Exception e) {
            e.printStackTrace();
        }



        return true;

    }
}
