package me.aimcventboat.main;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.listener.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import me.aimcventboat.main.commands.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.List;

import static me.aimcventboat.main.fonction.*;
import static me.aimcventboat.main.listener.villageropen.Cooldowns;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        setup();

        System.out.println("Plugin de course de bateaux lancé.");

        getCommand("test").setExecutor(new Test());

        getCommand("boat_start").setExecutor(new boat_start());
        getCommand("boat_start").setTabCompleter(new boat_start());

        getCommand("boat_end").setExecutor(new boat_end());

        getCommand("set_start").setExecutor(new set_start());

        getCommand("pnj_summon").setExecutor(new pnjsum());

        getCommand("checkpoint_pass").setExecutor(new checkpoint_pass());

        getCommand("run_add").setExecutor(new run_add());

        getCommand("competition").setExecutor(new competition());

        getCommand("compet_join").setExecutor(new compet_join());

        getCommand("compet_start").setExecutor(new compet_start());

        getCommand("hologram").setExecutor(new hologram());

        getCommand("hologram_rm").setExecutor(new hologram_rm());

        getCommand("hologram_menu").setExecutor(new hologram_menu());

        getCommand("run_rm").setExecutor(new run_rm());

        getServer().getPluginManager().registerEvents(new test(), this);

        getServer().getPluginManager().registerEvents(new clickevent(this), this);
        getServer().getPluginManager().registerEvents(new villageropen(), this);
        getServer().getPluginManager().registerEvents(new shift(), this);
        getServer().getPluginManager().registerEvents(new playerquit(), this);
        getServer().getPluginManager().registerEvents(new playermove(), this);
        getServer().getPluginManager().registerEvents(new interactevent(), this);


        FileConfiguration playerlist = ConfigGet("playerlist");

        for (String str : playerlist.getKeys(false)) {
            MemorySection player = (MemorySection) playerlist.get(str);
            player = MemoryToConfig(player);

            System.out.println(player);

                FileConfiguration run = ConfigGet("./runs/" + player.get("race"));

                ((List<String>)run.get("players")).remove(player.get("name"));

                ConfigSave(run, "./runs/" + player.get("race"));

            playerlist.set((String) player.get("name"), null);
        }

        FileConfiguration data = ConfigGet("./runs/data");

        for (String str : (List<String>) data.get("runslist")) {
            FileConfiguration run = ConfigGet("./runs/" + str);
            run.set("compet", false);
            run.set("started", true);

            ConfigSave(run, "./runs/" + str);

        }

        ConfigSave(playerlist, "playerlist");



        //Time Action bar
        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration playerlist = ConfigGet("playerlist");

                 for (String str : playerlist.getKeys(false)) {
                    MemorySection playerinfo = (MemorySection) playerlist.get(str);
                    playerinfo = MemoryToConfig(playerinfo);

                    Player player = getServer().getPlayer((String) playerinfo.get("name"));

                     FileConfiguration run = ConfigGet("./runs/" + playerinfo.get("race"));

                     if ((Boolean) run.get("compet") && !(Boolean)run.get("started")) {
                         continue;
                     }

                    final long startTimeMillis = Cooldowns.get(player.getName());

                    final long endTimeMillis = System.currentTimeMillis();

                    final long time = endTimeMillis - startTimeMillis;

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(AffichageTime(time)));
                }
            }


        }.runTaskTimer(this, 0, 1*20);


        //Checkpoints
        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration playerlist = ConfigGet("playerlist");

                for (String str : playerlist.getKeys(false)) {
                    MemorySection playerinfo = (MemorySection) playerlist.get(str);
                    playerinfo = MemoryToConfig(playerinfo);

                    Player player = getServer().getPlayer((String) playerinfo.get("name"));

                    FileConfiguration run = ConfigGet("./runs/" + playerinfo.get("race"));

                    if (!(Boolean) run.get("compet")) {
                        continue;
                    }

                    List<Double> coord = new ArrayList<>();
                    coord.add(player.getLocation().getX());
                    coord.add(player.getLocation().getY());
                    coord.add(player.getLocation().getZ());

                    playerinfo.addDefault("checkpoint", coord);
                    playerinfo.addDefault("checkpoint_rotation", player.getLocation().getYaw());

                    playerlist.addDefault(player.getName(), playerinfo);

                    BufferedWriter writer = null;

                    ConfigSave(playerlist, "playerlist");
                }
            }


        }.runTaskTimer(this, 0, 60*20);


        //hologram
        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration hologram_data = ConfigGet("hologram");

                for (String str : hologram_data.getKeys(false)) {

                    ArmorStand hologram = (ArmorStand) Bukkit.getEntity(UUID.fromString(str));

                    FileConfiguration hologram_json = MemoryToConfig((MemorySection) hologram_data.get(str));

                    FileConfiguration run = ConfigGet("./runs/" + hologram_json.get("race"));

                    try{
                        String name = (String) run.get("name");
                        if (name == null) {
                            hologram.setCustomName("§6Meilleurs scores de la course n°" + hologram_json.get("race"));
                        } else {
                        hologram.setCustomName("§6Meilleurs scores de la course " + name);}
                    } catch (Exception e) {
                        hologram.setCustomName("§6Meilleurs scores de la course n°" + hologram_json.get("race"));
                    }

                        FileConfiguration scores = MemoryToConfig((MemorySection) run.get("scores"));

                        HashMap<Long, String> scores2 = new HashMap<>();

                        ArrayList<Long> times = new ArrayList<>();

                        for (String player_name : scores.getKeys(false)) {
                            times.add(Long.valueOf(String.valueOf(scores.get(player_name))));

                            scores2.put(Long.valueOf(String.valueOf(scores.get(player_name))), player_name);
                        }

                        Collections.sort(times);

                        for (int i = 0; i < 6; i++) {
                            if (times.size() <= i) continue;

                            Color color = new Color(249, 168, 37);
                            if (i == 1) color = new Color(229,231,233);
                            else if (i == 2) color = new Color(220,118,51);
                            else if (i >= 3) color = new Color(105, 105, 105);

                            ArmorStand line = (ArmorStand) Bukkit.getEntity(UUID.fromString(((List<String>) hologram_json.get("lines")).get(i + 1)));

                            line.setCustomName(ChatColor.of(color) + "" + (i + 1) + "#" + AffichageTime(times.get(i)) + " | " + scores2.get(times.get(i)));

                        }

                    }

            }

        }.runTaskTimer(this, 5*20, 5*20);

        System.out.println("///////////////////////////////////////////////////");
        System.out.println("");
        System.out.println("Plugin by LoliChann from Cactus TEAM");
        System.out.println("");
        System.out.println("///////////////////////////////////////////////////");

    }

    @Override
    public void onDisable() {
        System.out.println("Plugin de course de bateaux arrêté.");
    }
}
