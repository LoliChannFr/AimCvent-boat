package me.aimcventboat.main.listener;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.material.PressureSensor;

public class test implements Listener {

    @EventHandler
    public void onEntityInteract(EntityInteractEvent e) {
        if (e.getEntity().getType() == EntityType.BOAT && e.getBlock().getType() == Material.ACACIA_PRESSURE_PLATE) {

            if (!(e.getEntity().getPassengers() instanceof Player)) return;

            Player p = (Player) e.getEntity().getPassengers();

            p.sendMessage("test");
            p.sendMessage(String.valueOf( e.getBlock().getBlockData()));
        }


    }
}
