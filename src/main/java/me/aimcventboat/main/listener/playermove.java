package me.aimcventboat.main.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.fonction;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class playermove implements Listener {

    @EventHandler
    public void onMenuClick(PlayerJumpEvent event) throws IOException {

        FileConfiguration playerlist = fonction.ConfigGet("playerlist");

        if (!playerlist.contains(event.getPlayer().getName())) {
            return;
        }

        try {
            FileConfiguration playerinfo = (FileConfiguration) playerlist.get(event.getPlayer().getName());

            if (playerinfo == null) return;

            FileConfiguration run = fonction.ConfigGet("./runs/" + playerinfo.get("race"));

            if ((Boolean) run.get("compet") && !((Boolean) run.get("started"))) {
                event.setCancelled(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
