package me.aimcventboat.main;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.listener.clickevent;
import me.aimcventboat.main.listener.playerquit;
import me.aimcventboat.main.listener.shift;
import me.aimcventboat.main.listener.villageropen;
import org.bukkit.plugin.java.JavaPlugin;
import me.aimcventboat.main.commands.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("Plugin de course de bateaux lancé.");

        getCommand("test").setExecutor(new Test());
        
        getCommand("boat_start").setExecutor(new boat_start());
        getCommand("boat_start").setTabCompleter(new boat_start());

        getCommand("boat_end").setExecutor(new boat_end());

        getCommand("set_start").setExecutor(new set_start());

        getCommand("pnj_summon").setExecutor(new pnjsum());

        getServer().getPluginManager().registerEvents(new clickevent(), this);
        getServer().getPluginManager().registerEvents(new villageropen(), this);
        getServer().getPluginManager().registerEvents(new shift(), this);
        getServer().getPluginManager().registerEvents(new playerquit(), this);


        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get("./plugins/AimCvent-boat/runs.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonObject parser = null;
        try {
            parser = (JsonObject) Jsoner.deserialize(reader);
        } catch (JsonException e) {
            e.printStackTrace();
        }

        HashMap<String, Integer> playerlist = (HashMap<String, Integer>) parser.get("playerlist");

        for (Map.Entry<String, Integer> player : playerlist.entrySet()) {
            JsonObject run = (JsonObject) parser.get(String.valueOf(player.getValue()));
            playerlist.remove(player.getKey());
            ((List<String>)run.get("players")).remove(player.getKey());


        }
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));
            Jsoner.serialize(parser, writer);
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        System.out.println("Plugin de course de bateaux arrêté.");
    }
}
