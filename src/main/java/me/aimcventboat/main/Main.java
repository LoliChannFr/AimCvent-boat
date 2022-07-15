package me.aimcventboat.main;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.listener.clickevent;
import me.aimcventboat.main.listener.playerquit;
import me.aimcventboat.main.listener.shift;
import me.aimcventboat.main.listener.villageropen;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import me.aimcventboat.main.commands.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

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

        JsonObject playerlist = (JsonObject) parser.get("playerlist");

        for (String str : playerlist.keySet()) {
            JsonObject player = (JsonObject) playerlist.get(str);
            JsonObject run = (JsonObject) parser.get(String.valueOf(player.get("race")));
            playerlist.remove(player.get("name"));
            ((List<String>)run.get("players")).remove(player.get("name"));


        }
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get("./plugins/AimCvent-boat/runs.json"));
            Jsoner.serialize(parser, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                long currenttime = Instant.now().getEpochSecond();
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

                JsonObject playerlist = (JsonObject) parser.get("playerlist");

                for (String str : playerlist.keySet()) {
                    JsonObject playerinfo = (JsonObject) playerlist.get(str);
                    System.out.println("test");
                    Player player = getServer().getPlayer((String) playerinfo.get("name"));
                    long time = (long) (currenttime - Long.valueOf(String.valueOf(playerinfo.get("time"))));
                    double time2 = (double) (System.currentTimeMillis()/1000 - Float.valueOf(String.valueOf(playerinfo.get("time"))));

                    int i = Math.toIntExact(Math.round(time));

                    TextComponent component = new TextComponent();

                    Integer min = (i - (i % 60)) / 60;
                    Integer sec = i % 60;

                    component.addExtra(min + ":");

                    if (sec <= 9){
                        component.addExtra("0" + sec);
                    }else component.addExtra(String.valueOf(sec));

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(component));
                }
            }


        }.runTaskTimer(this, 0, 1*20);

    }

    @Override
    public void onDisable() {
        System.out.println("Plugin de course de bateaux arrêté.");
    }
}
