package me.aimcventboat.main;

import org.bukkit.plugin.java.JavaPlugin;
import me.aimcventboat.main.commands.*;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("Plugin de course de bateaux lancé.");

        getCommand("test").setExecutor(new Test());
        
        getCommand("boat_start").setExecutor(new boat_start());
        getCommand("boat_start").setTabCompleter(new boat_start());

        getCommand("set_start").setExecutor(new set_start());
        getCommand("set_start").setTabCompleter(new set_start());
    }

    @Override
    public void onDisable() {
        System.out.println("Plugin de course de bateaux arrêté.");
    }
}
