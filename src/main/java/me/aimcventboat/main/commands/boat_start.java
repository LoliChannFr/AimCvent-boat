package me.aimcventboat.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class boat_start implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player){

            Player player = (Player) sender;


            player.sendMessage("Test");

        }

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("boat_start")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("prout");
                arguments.add("fesse");

                return arguments;
            }
        }

        return null;
    }
}
