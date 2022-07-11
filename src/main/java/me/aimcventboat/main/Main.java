package me.aimcventboat.main;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;
import me.aimcventboat.main.commands.*;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("Plugin de course de bateaux lancé.");

        getCommand("test").setExecutor(new Test());
        getCommand("test").setExecutor(new Test());

    }

    @Override
    public void onDisable() {
        System.out.println("Plugin de course de bateaux arrêté.");
    }
}
