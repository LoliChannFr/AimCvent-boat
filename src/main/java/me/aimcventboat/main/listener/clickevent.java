package me.aimcventboat.main.listener;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import me.aimcventboat.main.Main;
import me.aimcventboat.main.fonction;
import org.bukkit.*;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.awt.Color;

import static me.aimcventboat.main.listener.interactevent.KickPlayerRun;
import static me.aimcventboat.main.listener.villageropen.Cooldowns;

public class clickevent implements Listener {

    private Main main;

    public clickevent(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) throws IOException {

        Player player = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez la course")) {
            e.setCancelled(true);


                // create a reader
                FileConfiguration data = fonction.ConfigGet("./runs/data");

                List<String> runs = (List<String>) data.get("runslist");

                String nb = String.valueOf(Objects.requireNonNull(e.getCurrentItem()).getItemMeta().getCustomModelData());

                if (!runs.contains(nb)) {

                    player.closeInventory();
                    player.sendMessage("La course que vous avez sélectionnée n'existe pas.");

                    return;
                }

                Inventory gui = Bukkit.createInventory(player, 9, "§8Choisissez l'emplacement");


                List<ItemStack> menu = new ArrayList<>();

                ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                ItemMeta metaglass = glass.getItemMeta();
                metaglass.setDisplayName("§f ");
                metaglass.setCustomModelData((Integer.valueOf(nb)));
                glass.setItemMeta(metaglass);

                menu.add(glass);
                menu.add(glass);

                for (int i = 1; i < 6; i++) {
                    ItemStack wrench = new ItemStack(Material.BLUE_WOOL);
                    ItemMeta metawrench = wrench.getItemMeta();
                    metawrench.setCustomModelData(i);
                    metawrench.setDisplayName("§fPosition #" + i);
                    wrench.setItemMeta(metawrench);

                    menu.add(wrench);

                }

                menu.add(glass);
                menu.add(glass);

                ItemStack[] menu_items = menu.toArray(new ItemStack[0]);

                gui.setContents(menu_items);
                player.openInventory(gui);

        }

        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez l'emplacement")) {

            e.setCancelled(true);

            String nb = String.valueOf(Objects.requireNonNull(e.getInventory().getContents()[0]).getItemMeta().getCustomModelData());

            // create a
            System.out.println(nb);
            FileConfiguration selected_run = fonction.ConfigGet("./runs/" + nb);

            String boat = String.valueOf(e.getCurrentItem().getItemMeta().getCustomModelData());

            try {

                MemorySection selected_place = (MemorySection) selected_run.get("start_place");

                selected_place = (FileConfiguration) fonction.MemoryToConfig(selected_place);

                List<Double> coord = new ArrayList<>();
                coord.add(player.getLocation().getX());
                coord.add(player.getLocation().getY());
                coord.add(player.getLocation().getZ());

                Float direction = player.getLocation().getYaw();

                selected_place.set(boat, coord);
                selected_run.set("start_direction",direction);
                selected_run.set("start_place", selected_place);

                fonction.ConfigSave(selected_run, "./runs/" + nb);

                player.sendMessage("Position de spawn de la course " + nb + " enregistrée !");

                player.closeInventory();

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }

        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez la course à lancer")) {
            e.setCancelled(true);

            // create a reader
            FileConfiguration data = fonction.ConfigGet("./runs/data");

            List<String> runs = (List<String>) data.get("runslist");

            String nb = String.valueOf(Objects.requireNonNull(e.getCurrentItem()).getItemMeta().getCustomModelData());

            if (!runs.contains(nb)) {

                player.closeInventory();
                player.sendMessage("La course que vous avez sélectionnée n'existe pas.");

                return;
            }

            World world = player.getWorld();
            Location loc = player.getLocation();

            Entity entity = world.spawnEntity(loc, EntityType.VILLAGER);
            Villager villager = (Villager) entity;
            villager.setProfession(Villager.Profession.ARMORER);
            villager.setVillagerType(Villager.Type.PLAINS);
            villager.setAI(false);
            villager.setCustomName("Cliquez pour rejoindre la course !");
            villager.setCustomNameVisible(true);
            villager.setInvulnerable(true);
            villager.setGravity(false);
            villager.setVillagerLevel(Integer.valueOf(nb));
            villager.setPersistent(true);
            player.closeInventory();

            List<Double> coord = new ArrayList<>();
            coord.add(loc.getX());
            coord.add(loc.getY());
            coord.add(loc.getZ());

            FileConfiguration selected_run = fonction.ConfigGet("./runs/" + nb);
            selected_run.set("backup-place",coord);

            fonction.ConfigSave(data,"./runs/data");
            fonction.ConfigSave(selected_run, "./runs/" + nb);

        }

        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez l'hologram")) {
            e.setCancelled(true);

            // create a reader
            FileConfiguration data = fonction.ConfigGet("./runs/data");

            List<String> runs = (List<String>) data.get("runslist");

            String nb = String.valueOf(Objects.requireNonNull(e.getCurrentItem()).getItemMeta().getCustomModelData());

            if (!runs.contains(nb)) {

                player.closeInventory();
                player.sendMessage("La course que vous avez sélectionnée n'existe pas.");

                return;
            }

            World world = player.getWorld();
            Location loc = player.getLocation().add(0,1,0);

            ArmorStand hologram = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
            hologram.setVisible(false);
            hologram.setCustomNameVisible(true);
            hologram.setCustomName(ChatColor.RED + "hologram");
            hologram.setGravity(false);

            List<String> lines = new ArrayList<>();

            for (int i = 1; i < 7; i++) {
                ArmorStand hologram2 = (ArmorStand) world.spawnEntity(loc.add(0,-0.25,0), EntityType.ARMOR_STAND);
                hologram2.setVisible(false);
                hologram2.setCustomNameVisible(true);
                hologram2.setCustomName(ChatColor.RED + "§c ");
                hologram2.setGravity(false);
                lines.add(String.valueOf(hologram2.getUniqueId()));
            }

            FileConfiguration hologram_data = fonction.ConfigGet("hologram");

            FileConfiguration hologram_json = new YamlConfiguration();
            hologram_json.options().copyDefaults(true);

            hologram_json.set("race", nb);
            hologram_json.set("lines", lines);

            hologram_data.set(String.valueOf(hologram.getUniqueId()), hologram_json);

            fonction.ConfigSave(data, "./runs/data");

            fonction.ConfigSave(hologram_data, "hologram");
            player.closeInventory();

        }

        if (e.getView().getTitle().equalsIgnoreCase("test")) {
            e.setCancelled(true);
            System.out.println("test12");
            // create a reader
            FileConfiguration data = fonction.ConfigGet("./runs/data");

            FileConfiguration hologram_data = fonction.ConfigGet("hologram");

            List<String> runs = (List<String>) data.get("runslist");

            String nb = String.valueOf(Objects.requireNonNull(e.getCurrentItem()).getItemMeta().getCustomModelData());
            System.out.println("test2");
            if (!runs.contains(nb)) {

                player.closeInventory();
                player.sendMessage("La course que vous avez sélectionnée n'existe pas.");

                return;
            }

            Inventory gui = Bukkit.createInventory(player, 54, "§bChoisissez l'hologram");
            System.out.println("test");
            List<ItemStack> menu = new ArrayList<>();
            System.out.println("test1");
            for (String str : hologram_data.getKeys(false)) {
                System.out.println(str);
                FileConfiguration hologram_json = fonction.MemoryToConfig((MemorySection) hologram_data.get(str));

                if (!hologram_json.get("race").equals(nb)) return;

                ItemStack wrench = new ItemStack(Material.ARMOR_STAND);
                ItemMeta metawrench = wrench.getItemMeta();
                metawrench.setDisplayName(str);
                wrench.setItemMeta(metawrench);

                menu.add(wrench);

            }
            System.out.println("test3");
            ItemStack[] menu_items = menu.toArray(new ItemStack[0]);

            gui.setContents(menu_items);
            player.openInventory(gui);
        }

        if (e.getView().getTitle().equalsIgnoreCase("§bChoisissez l'hologram")) {

            e.setCancelled(true);

            try {

                try {
                    Inventory gui = Bukkit.createInventory(player, 9, "§8Menu hologram");

                    ItemStack armorstand = new ItemStack(Material.ARMOR_STAND);
                    ItemMeta metastand = armorstand.getItemMeta();
                    metastand.setDisplayName(e.getCurrentItem().getItemMeta().getDisplayName());
                    armorstand.setItemMeta(metastand);

                    ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                    ItemMeta metaglass = glass.getItemMeta();
                    metaglass.setDisplayName("§f ");
                    glass.setItemMeta(metaglass);

                    ItemStack tp = new ItemStack(Material.ENDER_PEARL);
                    ItemMeta metatp = tp.getItemMeta();
                    metatp.setDisplayName("§fSe téléporter à l'hologram");
                    tp.setItemMeta(metatp);

                    ItemStack remove = new ItemStack(Material.BARRIER);
                    ItemMeta metaremove = remove.getItemMeta();
                    metaremove.setDisplayName("§fRetirer l'hologram");
                    remove.setItemMeta(metaremove);

                    ItemStack[] menu_items = {glass, glass, tp, glass, armorstand, glass, remove, glass, glass};

                    gui.setContents(menu_items);
                    player.openInventory(gui);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (e.getView().getTitle().equalsIgnoreCase("§8Menu hologram")) {

            e.setCancelled(true);

            // create a reader
            FileConfiguration hologram_data = fonction.ConfigGet("hologram");

            UUID uuid = UUID.fromString(e.getInventory().getItem(4).getItemMeta().getDisplayName());

            FileConfiguration hologram_json = fonction.MemoryToConfig((MemorySection) hologram_data.get(e.getInventory().getItem(4).getItemMeta().getDisplayName()));

            try {
                ArmorStand hologram = (ArmorStand) Bukkit.getEntity(uuid);

                if (e.getCurrentItem().getType().equals(Material.ENDER_PEARL)) {
                    player.teleport(hologram.getLocation());
                    player.closeInventory();
                }

                if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
                    hologram.remove();

                    for (String str : (ArrayList<String>) hologram_json.get("lines")) {
                        ArmorStand line = (ArmorStand) Bukkit.getEntity(UUID.fromString(str));
                        line.remove();
                    }

                    hologram_data.set(e.getInventory().getItem(4).getItemMeta().getDisplayName(), null);
                    player.closeInventory();

                    fonction.ConfigSave(hologram_data,"hologram");
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }

        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez la compétition")) {
            e.setCancelled(true);

            if (player instanceof Player){

                if (!player.isOp()) return;

                player = Bukkit.getPlayer(Objects.requireNonNull(e.getInventory().getItem(53)).getItemMeta().getDisplayName());

                // create a reader
                FileConfiguration data = fonction.ConfigGet("./runs/data");

                List<String> runs = (List<String>) data.get("runslist");

                String nb = String.valueOf(e.getCurrentItem().getItemMeta().getCustomModelData());

                if (runs.contains(nb)) {

                    FileConfiguration run = fonction.ConfigGet("./runs/" + nb);

                    FileConfiguration start_place = fonction.MemoryToConfig((MemorySection) run.get("start_place"));

                    Integer place = ((List<String>)run.get("players")).size() + 1;

                    if (!(Boolean) run.get("compet")) {
                        player.sendMessage("§cLa course n'est pas en mode compétition.");
                        return;
                    }

                    if (place > 5){
                        player.sendMessage("La course est pleine.");
                        return;
                    }



                    player.sendMessage(String.valueOf(place));

                    List<Double> coord = (List<Double>) start_place.get(String.valueOf(place));

                    Location loc = player.getLocation();
                    loc.setX(Float.valueOf(String.valueOf(coord.get(0)))); loc.setY(Float.valueOf(String.valueOf(coord.get(1)))); loc.setZ(Float.valueOf(String.valueOf(coord.get(2))));
                    loc.setYaw(Float.valueOf(String.valueOf(run.get("start_direction"))));

                    player.teleport(loc);

                    ((LivingEntity) player).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000000, 10));

                    BufferedWriter writer = null;

                    JsonObject playerinfo = new JsonObject();

                    final long startTimeMillis = System.currentTimeMillis();

                    playerinfo.put("race",nb);
                    playerinfo.put("time",startTimeMillis);
                    playerinfo.put("name",player.getName());
                    playerinfo.put("checkpoint","0");

                    //Inventory items
                    ItemStack barrier = new ItemStack(Material.BARRIER);
                    ItemMeta meta = barrier.getItemMeta();
                    meta.setDisplayName("§cQuitter la course");
                    barrier.setItemMeta(meta);

                    player.getInventory().clear();
                    player.getInventory().setItem(8,barrier);

                    FileConfiguration playerlist = fonction.ConfigGet("playerlist");

                    playerlist.set(player.getName(), playerinfo);
                    ((List<String>)run.get("players")).add(player.getName());

                    HashMap<String, Integer> pl = new HashMap<>();
                    pl.put(player.getName(), Integer.valueOf(nb));

                    fonction.ConfigSave(run, "./runs/" + nb);
                    fonction.ConfigSave(playerlist, "playerlist");

                }

            }

            return;
        }

        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez la compétition à démarrer")) {
            e.setCancelled(true);
            if (player instanceof Player){
                player.closeInventory();
                if (!player.isOp()) return;

                // create a reader
                FileConfiguration data = fonction.ConfigGet("./runs/data");

                List<String> runs = (List<String>) data.get("runslist");

                String nb = String.valueOf(e.getCurrentItem().getItemMeta().getCustomModelData());

                if (runs.contains(nb)) {

                    FileConfiguration run = fonction.ConfigGet("./runs/" + nb);

                    run.set("scores_compet", null);

                    if (!(Boolean) run.get("compet")) {
                        player.sendMessage("§cLa course n'est pas en mode compétition.");
                        return;
                    }

                    World world = player.getWorld();



                    BufferedWriter writer = null;

                    FileConfiguration playerinfo = new YamlConfiguration();
                    playerinfo.options().copyDefaults(true);

                    playerinfo.set("checkpoint","0");
                    playerinfo.set("race",nb);

                    FileConfiguration playerlist = fonction.ConfigGet("playerlist");

                    FileConfiguration finalRun = run;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            for (String str : (List<String>) finalRun.get("players")) {
                                Player player = Bukkit.getPlayer(str);

                                Color color = new Color(233, 30, 99);
                                Color color2 = new Color(255, 202, 40);

                                player.sendTitle(net.md_5.bungee.api.ChatColor.of(color) + "La course va commencer !",net.md_5.bungee.api.ChatColor.of(color2) + "3 !");
                            }
                        }
                    }.runTaskLater(main, 0);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            for (String str : (List<String>) finalRun.get("players")) {
                                Player player = Bukkit.getPlayer(str);

                                Color color = new Color(233, 30, 99);
                                Color color2 = new Color(255, 152, 0);

                                player.sendTitle(net.md_5.bungee.api.ChatColor.of(color) + "La course va commencer !",net.md_5.bungee.api.ChatColor.of(color2) + "2 !");
                            }
                        }
                    }.runTaskLater(main, 20);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            for (String str : (List<String>) finalRun.get("players")) {
                                Player player = Bukkit.getPlayer(str);

                                Color color = new Color(233, 30, 99);
                                Color color2 = new Color(255, 87, 34);

                                player.sendTitle(net.md_5.bungee.api.ChatColor.of(color) + "La course va commencer !",net.md_5.bungee.api.ChatColor.of(color2) + "1 !");
                            }
                        }
                    }.runTaskLater(main, 40);
                    FileConfiguration finalParser = playerlist;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            final long startTimeMillis = System.currentTimeMillis();

                            for (String str : (List<String>) finalRun.get("players")) {
                                Player player = Bukkit.getPlayer(str);

                                Color color = new Color(233, 30, 99);

                                player.sendTitle(net.md_5.bungee.api.ChatColor.of(color) + "C'est partie !", "");
                            }
                            for (String str : (List<String>) finalRun.get("players")) {
                                Player player = Bukkit.getPlayer(str);

                                Location loc = player.getLocation();
                                Location loc2 = player.getLocation();
                                loc2.setY(loc2.getY() + 2);

                                player.teleport(loc2);

                                Entity boat = world.spawnEntity(loc, EntityType.BOAT);

                                boat.addPassenger(player);

                                ((LivingEntity) player).removePotionEffect(PotionEffectType.SLOW);
                            }

                            for (String str : (List<String>) finalRun.get("players")) {
                                Player player = Bukkit.getPlayer(str);


                                ItemStack checkpoint = new ItemStack(Material.ENDER_PEARL);
                                ItemMeta meta = checkpoint.getItemMeta();
                                meta.setDisplayName("§5Retour au dernier checkpoint");
                                checkpoint.setItemMeta(meta);
                                player.getInventory().setItem(8, checkpoint);

                                Cooldowns.put(player.getName(), startTimeMillis);

                                playerinfo.set("time",startTimeMillis);
                                playerinfo.set("name",player.getName());

                                finalParser.set(player.getName(), playerinfo);
                            }

                            finalRun.set("started", true);

                            fonction.ConfigSave(finalRun,"./runs/" + nb);

                            fonction.ConfigSave(finalParser, "playerlist");
                        }
                    }.runTaskLater(main, 60);
                }
            }
            return;
        }

        if (e.getView().getTitle().equalsIgnoreCase("§8Mode compétition")) {

            e.setCancelled(true);

            String nb = String.valueOf(Objects.requireNonNull(e.getCurrentItem()).getItemMeta().getCustomModelData());

            // create a reader
            FileConfiguration run = fonction.ConfigGet("./runs/" + nb);

            String boat = String.valueOf(e.getCurrentItem().getItemMeta().getCustomModelData());

            try {
                Boolean compet = false;

                try{ compet = (Boolean) run.get("compet");}
                catch (Exception ex) {
                    compet = false;
                }

                if (compet) {
                    compet = false;
                }
                else {
                    compet = true;

                    List<String> players = (List<String>) run.get("players");


                    FileConfiguration playerlist = fonction.ConfigGet("playerlist");

                    for (String player_name : players) {
                        KickPlayerRun(Bukkit.getPlayer(player_name), playerlist);
                    }
                }

                run.set("compet",compet);
                run.set("started",!compet);

                fonction.ConfigSave(run, "./runs/" + nb);

                if (compet) player.sendMessage("§cCourse passée en mode compétition !");
                else  player.sendMessage("§cCourse sortie du mode compétition !");

                player.closeInventory();

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }

        if (e.getView().getTitle().equalsIgnoreCase("§8Choisissez la course à retirer")) {
            e.setCancelled(true);

            ItemStack item = e.getCurrentItem();

            File folder = Bukkit.getServer().getPluginManager().getPlugin("AimCvent-Boat").getDataFolder();

            File f = new File (folder, "./runs/" + item.getItemMeta().getCustomModelData() + ".yml");

            Files.delete(Paths.get(f.getPath()));

            // create a reader

            // create parser
            FileConfiguration data = fonction.ConfigGet("./runs/data");

            List<String> runslist = (List<String>) data.get("runslist");

            runslist.remove(String.valueOf(item.getItemMeta().getCustomModelData()));

            data.set("runslist", runslist);

            fonction.ConfigSave(data,"./runs/data");


            f.delete();

            player.closeInventory();
        }


    }
}
