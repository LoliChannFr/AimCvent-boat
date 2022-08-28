package me.aimcventboat.main.commands;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.fonction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class run_add implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = (Player) sender;

        FileConfiguration data = fonction.ConfigGet("./runs/data");

        List<String> runslist = (List<String>) data.get("runslist");

        MemorySection newfile = (MemorySection) data.get("template");

        String name = null;

        if (args.length > 0) {

            name = "";

            for (String string : args) {

                name = name + " " + string;
            }

        }
        newfile.set("name", name);

        String nb = String.valueOf(runslist.size() + 1);

        runslist.add(nb);

        fonction.ConfigCreate("./runs/" + nb);

        fonction.ConfigSave(data, "./runs/data");

        FileConfiguration fc = new YamlConfiguration();

        for (String f : newfile.getKeys(false)) {
            fc.addDefault(f, newfile.get(f));
        }

        fonction.ConfigSave(fc, "./runs/" + nb);

        player.sendMessage("Course ajoutée !");

        return false;
    }
}
