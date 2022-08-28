package me.aimcventboat.main.listener;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.fonction;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.List;

import static me.aimcventboat.main.fonction.AffichageTime;
import static me.aimcventboat.main.fonction.MemoryToConfig;


public class villageropen implements Listener {

    public static Map<String, Long> Cooldowns = new HashMap<String, Long>();

    @EventHandler
    public void onVillager(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity entity = e.getRightClicked();

        if (entity.getType().equals(EntityType.VILLAGER)) {
            if (entity.getName().equals("Cliquez pour rejoindre la course !")) {
                e.setCancelled(true);
                
                Villager villager = (Villager) entity;
                String nb = String.valueOf(villager.getVillagerLevel());

                // create a reader
                FileConfiguration data = fonction.ConfigGet("./runs/data");

                List<String> runs = (List<String>) data.get("runslist");

                if (runs.contains(nb)) {

                    FileConfiguration run = fonction.ConfigGet("./runs/" + nb);

                    MemorySection start_place = (MemorySection) run.get("start_place");

                    start_place = MemoryToConfig(start_place);

                    Integer place = ((List<String>)run.get("players")).size() + 1;

                    if ((Boolean) run.get("compet")) {
                        player.sendMessage("§cLa course est en mode compétition.");
                        return;
                    }

                    if (place > 5){
                        player.sendMessage("La course est pleine.");
                        return;
                    }



                    List<Double> coord = (List<Double>) start_place.get(String.valueOf(place));

                    if (coord == null) {
                        player.sendMessage("§cIl n'y a pas de positions de départ pour cette course.");
                        return;
                    }

                    Location loc = player.getLocation();
                    loc.setX(Float.valueOf(String.valueOf(coord.get(0)))); loc.setY(Float.valueOf(String.valueOf(coord.get(1)))); loc.setZ(Float.valueOf(String.valueOf(coord.get(2))));
                    loc.setYaw(Float.valueOf(String.valueOf(run.get("start_direction"))));

                    player.teleport(loc);

                    World world = player.getWorld();

                    Entity boat = world.spawnEntity(loc, EntityType.BOAT);

                    boat.addPassenger(player);

                    FileConfiguration playerinfo = new YamlConfiguration();

                    final long startTimeMillis = System.currentTimeMillis();

                    Cooldowns.put(player.getName(), startTimeMillis);

                    playerinfo.addDefault("race",nb);
                    playerinfo.addDefault("time",startTimeMillis);
                    playerinfo.addDefault("name",player.getName());
                    playerinfo.addDefault("checkpoint","0");
                    playerinfo.options().copyDefaults(true);

                    //scoreboard
                    ScoreboardManager scmanager = Bukkit.getScoreboardManager();
                    Scoreboard scoreboard = scmanager.getNewScoreboard();

                    String time = null;

                    try {
                        MemorySection scores = (MemorySection) run.get("scores") ;
                        scores = MemoryToConfig(scores);


                         time = AffichageTime(Long.valueOf(String.valueOf(scores.get(player.getName()))));
                    } catch (NumberFormatException ex) {
                        //test
                    }
                    Objective title = scoreboard.registerNewObjective("title", "title", "§c§lCourse n°" + nb);
                    title.setDisplaySlot(DisplaySlot.SIDEBAR);
                    Score besttime = title.getScore("Votre meilleur temps : " );
                    besttime.setScore(4);
                    if (time == null) time = "null";
                    Score besttimevalue = title.getScore(time);
                    besttimevalue.setScore(3);
                    Score space = title.getScore(" ");
                    space.setScore(5);
                    Score space2 = title.getScore(" ");
                    space2.setScore(2);
                    Color discordcolor = new Color(85, 97, 245);
                    Score discord = title.getScore(ChatColor.of(discordcolor) + "discord.AimCvent.fr");
                    discord.setScore(1);


                    player.setScoreboard(scoreboard);

                    FileConfiguration playerlist = fonction.ConfigGet("playerlist");

                    playerinfo.options().copyDefaults(true);
                    playerlist.addDefault(player.getName(), playerinfo);
                    ((List<String>)run.get("players")).add(player.getName());

                    HashMap<String, Integer> pl = new HashMap<>();
                    pl.put(player.getName(), Integer.valueOf(nb));

                    //Inventory items
                    ItemStack barrier = new ItemStack(Material.BARRIER);
                    ItemMeta meta = barrier.getItemMeta();
                    meta.setDisplayName("§cQuitter la course");
                    barrier.setItemMeta(meta);

                    ItemStack checkpoint = new ItemStack(Material.ENDER_PEARL);
                    meta = barrier.getItemMeta();
                    meta.setDisplayName("§5Retour au dernier checkpoint");
                    checkpoint.setItemMeta(meta);

                    player.getInventory().clear();
                    player.getInventory().setItem(8,barrier);
                    player.getInventory().setItem(0, checkpoint);

                    fonction.ConfigSave(run, "./runs/" + nb);
                    fonction.ConfigSave(playerlist, "playerlist");

                }
            }
        }
    }
}
