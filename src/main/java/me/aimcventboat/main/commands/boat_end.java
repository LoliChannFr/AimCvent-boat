package me.aimcventboat.main.commands;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class boat_end implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            return true;
        }

        try{
            Player target = Bukkit.getPlayer(args[0]);

            Reader reader = null;
            try {
                reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // create parser
            JsonObject parser = null;
            try {
                parser = (JsonObject) Jsoner.deserialize(reader);
            } catch (JsonException e) {
                e.printStackTrace();
            }

            JsonObject playerlist = (JsonObject) parser.get("playerlist");

            if (!playerlist.containsKey(target.getName())) {
                return true;
            }

            JsonObject playerinfo = (JsonObject) playerlist.get(target.getName());

            double time = (double) (System.currentTimeMillis() - Float.valueOf(String.valueOf(playerinfo.get("time"))));

            int i = Math.toIntExact(Math.round(time));

            target.sendMessage(String.valueOf(i));






        } catch (Exception e) {
            e.printStackTrace();
        }



        return true;

    }
}
