package me.aimcventboat.main.listener;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.fonction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class interactevent implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) throws InterruptedException {

        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack it = event.getItem();

        if (it == null) return;

        Reader reader = null;

        FileConfiguration playerlist = fonction.ConfigGet("playerlist");
        if (!playerlist.contains(player.getName())) {
            return;
        }

        event.setCancelled(true);


        if (it.getType() == Material.BARRIER) {

            KickPlayerRun(player, playerlist);


        }

        if (it.getType() == Material.ENDER_PEARL) {
            FileConfiguration playerinfo = (FileConfiguration) playerlist.get(player.getName());

            List<Double> coord = (List<Double>) playerinfo.get("checkpoint");

            BigDecimal rotation = (BigDecimal) playerinfo.get("checkpoint_rotation");

            Vehicle vehicle = (Vehicle) player.getVehicle();

            Location loc = player.getLocation();

            loc.setX(Float.valueOf(String.valueOf(coord.get(0)))); loc.setY(Float.valueOf(String.valueOf(coord.get(1)))); loc.setZ(Float.valueOf(String.valueOf(coord.get(2))));
            loc.setYaw(Float.valueOf(String.valueOf(rotation)));

            vehicle.teleport(loc);

            vehicle.setPassenger(player);
        }
    }

    public static void KickPlayerRun(Player player, FileConfiguration playerlist) {
        try {

            Vehicle vehicle = (Vehicle) player.getVehicle();

            MemorySection playerinfo = ((MemorySection) playerlist.get(player.getName()));

            playerinfo = fonction.MemoryToConfig(playerinfo);

            String str = String.valueOf(playerinfo.get("race"));
            System.out.println(str);
            FileConfiguration run = fonction.ConfigGet("./runs/" + str);

            if (!playerlist.contains(player.getName())) {
                return;
            }

            ((List<String>) run.get("players")).remove(player.getName());

            playerlist.set(player.getName(), null);

            List<Double> coord = (List<Double>) run.get("backup-place");

            Location loc = player.getLocation();
            loc.setX(Float.valueOf(String.valueOf(coord.get(0))));
            loc.setY(Float.valueOf(String.valueOf(coord.get(1))));
            loc.setZ(Float.valueOf(String.valueOf(coord.get(2))));

            if (vehicle != null) {
            vehicle.remove();}

            fonction.ConfigSave(run , "./runs/" + str);
            fonction.ConfigSave(playerlist,"playerlist");


            player.teleport(loc);

            player.sendMessage("Vous avez été retiré de la course.");

            ScoreboardManager scmanager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = scmanager.getNewScoreboard();

            ((Player) player).setScoreboard(scoreboard);

            player.getInventory().clear();

            return;

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
