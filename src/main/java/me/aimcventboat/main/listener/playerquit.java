package me.aimcventboat.main.listener;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.fonction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class playerquit implements Listener {

    @EventHandler
    public void OnPlayerQuit(PlayerConnectionCloseEvent e) {


        FileConfiguration playerlist = fonction.ConfigGet("playerlist");

        if (playerlist.contains(e.getPlayerName())) {
            Player player = Bukkit.getPlayer(e.getPlayerUniqueId());

            String str = String.valueOf(((FileConfiguration) playerlist.get(player.getName())).get("race"));

            FileConfiguration run = fonction.ConfigGet("./runs/" + ((FileConfiguration) playerlist.get(player.getName())).get("race"));

            ((List<String>) run.get("players")).remove(player.getName());

            playerlist.addDefault(e.getPlayerName(), null);

            fonction.ConfigSave(playerlist, "playerlist");

            fonction.ConfigSave(run, "./runs/" + ((FileConfiguration) playerlist.get(player.getName())).get("race"));
        }

    }

}
