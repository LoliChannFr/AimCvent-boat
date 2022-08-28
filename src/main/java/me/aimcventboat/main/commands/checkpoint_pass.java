package me.aimcventboat.main.commands;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class checkpoint_pass implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = null;
        if (args.length > 1) player = Bukkit.getPlayer(args[1]);
        else player = (Player) sender;

        if (sender instanceof BlockCommandSender) {
            Location loc = ((BlockCommandSender) sender).getBlock().getLocation();
            Entity boat = (Entity) loc.getWorld().getNearbyEntities(loc, 1, 2, 1).stream()
                    .filter(e -> e.getType() == EntityType.BOAT)
                    .findFirst()
                    .orElse(null);

            System.out.println(boat.getPassengers());
            System.out.println(boat.getPassengers().get(0));
            System.out.println(boat.getPassengers().size());
            if (boat.getPassengers().get(0) instanceof Player) {
                player = (Player) boat.getPassengers().get(0);
            }
            player = (Player) Bukkit.getPlayer(boat.getPassengers().get(0).getName());
        }

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

        JsonObject playerinfo = (JsonObject) ((JsonObject) parser.get("playerlist")).get(player.getName());
        player.sendMessage(String.valueOf(playerinfo));
        playerinfo.put("checkpoint",String.valueOf(Integer.parseInt((String) playerinfo.get("checkpoint")) + 1));
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));
            Jsoner.serialize(parser, writer);
            writer.close();
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
