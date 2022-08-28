package me.aimcventboat.main;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class fonction {

    public static Inventory RunMenu(String name, Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, name);


            // create a reader
            FileConfiguration data = ConfigGet("./runs/data");


            List<String> runs = (List<String>) data.get("runslist");

            List<ItemStack> menu = new ArrayList<>();



            for (String i : runs) {

                FileConfiguration run = ConfigGet("./runs/" + i);

                ItemStack wrench = new ItemStack(Material.BLUE_WOOL);

                if ((Boolean) run.get("compet")) {
                    wrench.setType(Material.GREEN_WOOL);
                }
                else {
                    wrench.setType(Material.BLUE_WOOL);
                }

                ItemMeta metawrench = wrench.getItemMeta();
                metawrench.setCustomModelData(Integer.valueOf(i));
                try{
                    String runname = (String) run.get("name");
                    metawrench.setDisplayName("§fCourse" + runname);
                    if (runname == null) {
                        metawrench.setDisplayName("§fCourse #" + i);
                    }
                } catch (Exception e) {
                    metawrench.setDisplayName("§fCourse #" + i);
                }

                wrench.setItemMeta(metawrench);

                menu.add(wrench);

            }
            ItemStack[] menu_items = menu.toArray(new ItemStack[0]);

            gui.setContents(menu_items);

        return gui;
    }

        public static String AffichageTime(long time) {
        int i = Math.toIntExact(Math.round(time));

        String component = null;

        Integer millisec = i % 1000;

        i = (i - millisec)/1000;

        Integer min = (i - (i % 60)) / 60;
        Integer sec = i % 60;

        component = min + ":";

        if (sec <= 9){
            component = component + "0" + sec + ":";
        }else component = component + sec + ":";

        if (millisec <= 9){
            component = component + "00" + millisec;
        }else if (millisec <= 99) component = component + "0" + millisec;
        else component = component + millisec;

        return component;
    }

    private static File file;
    private static FileConfiguration customFile;
    private static File folder;

    public static void setup(){

        folder = Bukkit.getServer().getPluginManager().getPlugin("AimCvent-Boat").getDataFolder();

        //Dossier config
        if (!folder.exists()) {
            try {
                Files.createDirectories(Path.of(folder.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File runfolder = new File (folder, "runs");

        if (!runfolder.exists()) {
            try {
                Files.createDirectories(Path.of(runfolder.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Fichier config
        List<File> files = new ArrayList<>();

        file = new File(folder, "test.yml");
        files.add(file);

        file = new File(folder, "hologram.yml");
        files.add(file);

        file = new File(folder, "playerlist.yml");
        files.add(file);

        file = new File(folder, "runs.yml");
        files.add(file);

        file = new File(folder,"./runs/data.yml");

        for (File f : files) {
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Fichier config créé");
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile();

                customFile = ConfigGet("./runs/data");

                FileConfiguration template = new YamlConfiguration();
                FileConfiguration template1 = new YamlConfiguration();
                template1.addDefault("template", 1000000000);

                template.addDefault("compet", false);
                template.addDefault("start_direction", 1);
                template1.options().copyDefaults(true);
                template.addDefault("scores", template1);
                template.addDefault("players", new ArrayList<>());
                template.addDefault("backup-place", new ArrayList<>());
                template.addDefault("start_place", new YamlConfiguration());
                template.addDefault("started", true);

                template.options().copyDefaults(true);

                customFile.addDefault("template", template);
                customFile.addDefault("runslist", new ArrayList<>());

                ConfigSave(customFile, "./runs/data");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ConfigCreate(String filename) {
        File file = new File(folder, filename + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Fichier config créé");
        }
    }

    public static FileConfiguration ConfigGet(String filename){
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("AimCvent-Boat").getDataFolder(), filename + ".yml");

        if (!file.exists()) {
            return null;
        }

        customFile = YamlConfiguration.loadConfiguration(file);
        customFile.options().copyDefaults(true);

        return customFile;
    }

    public static String ConfigSave(FileConfiguration cfg, String filename){
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("AimCvent-Boat").getDataFolder(), filename + ".yml");

        if (!file.exists()) {
            return ("file doesn't exist");
        }

        cfg.options().copyDefaults(true);

        try {
            cfg.save(file);
            System.out.println(cfg.getDefaults());
            System.out.println("fichier" + filename + "modifié");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }

    public static String ConfigReload(String filename){
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("AimCvent-Boat").getDataFolder(), filename + ".yml");

        if (!file.exists()) {
            return ("file doesn't exist");
        }

        customFile = YamlConfiguration.loadConfiguration(file);
        return filename;
    }

    public static FileConfiguration MemoryToConfig(MemorySection memorySection) {
        FileConfiguration fileConfiguration = new YamlConfiguration();

        for (String f : memorySection.getKeys(false)) {
            fileConfiguration.addDefault(f, memorySection.get(f));
        }

        fileConfiguration.options().copyDefaults(true);

        return fileConfiguration;
    }

}

