package me.aimcventboat.main.commands;


import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class set_start implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (sender instanceof Player){

            Player player = (Player) sender;


            try {
                // create a reader
                Reader reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));


                // create parser
                JsonObject parser = (JsonObject) Jsoner.deserialize(reader);


                List<String> runs = (List<String>) parser.get("runslist");

                try {
                    if (runs.contains(((String) args[0]))) {
                        try {

                            JsonObject selected_run = (JsonObject) parser.get(args[0]);

                            List<Double> coord = new ArrayList<>();
                            coord.add(player.getLocation().getX());
                            coord.add(player.getLocation().getY());
                            coord.add(player.getLocation().getZ());

                            Float direction = player.getLocation().getYaw();

                            selected_run.put("start_place",coord);
                            selected_run.put("start_direction",direction);

                            parser.put(args[0], selected_run);

                            BufferedWriter writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));

                            Jsoner.serialize(parser, writer);

                            writer.close();
                            reader.close();

                            player.sendMessage("Position de spawn de la course ", args[0], "enregistrée !");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        player.sendMessage("La course que vous cherchez n'existe pas !");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("La course que vous cherchez n'existe pas !");
                }
            } catch (IOException | JsonException e) {
                e.printStackTrace();
            }


        }


        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("set_start")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();

                try {
                    // create a reader
                    Reader reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));

                    // create parser
                    JsonObject parser = (JsonObject) Jsoner.deserialize(reader);

                    List<String> runs = (List<String>) parser.get("runslist");

                    reader.close();
                    return runs;
                } catch (IOException | JsonException e) {
                    e.printStackTrace();
                }
            }

            if (args.length == 2) {
                List<String> arguments = new ArrayList<>();

                arguments.add("~");

                return arguments;
            }

            if (args.length == 3) {
                List<String> arguments = new ArrayList<>();

                arguments.add("~");

                return arguments;
            }

            if (args.length == 4) {
                List<String> arguments = new ArrayList<>();

                arguments.add("~");

                return arguments;
            }
        }
        return null;
    }
}
