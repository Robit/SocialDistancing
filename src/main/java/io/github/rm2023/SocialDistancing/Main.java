package io.github.rm2023.SocialDistancing;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
    File configFile;
    YamlConfiguration config;
    boolean errorOnLoad = true;
    boolean enabled = false;
    double distance = 0;
    double power = 0;
    boolean yCancelled = false;
    Sound sound = null;

    @Override
    public void onLoad() {
        configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.saveResource("config.yml", false);
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            this.getLogger().log(Level.SEVERE, "Error loading config information!");
            e.printStackTrace();
            return;
        }
        loadConfig();
        errorOnLoad = false;
    }

    @Override
    public void onEnable() {
        if (errorOnLoad) {
            return;
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().addPermission(new Permission("socialdistancing.disable", "Disables social distancing enforcement for that player"));
        getServer().getPluginManager().addPermission(new Permission("socialdistancing.toggle", "Allows that player to toggle social distancing for the server by using /toggleSocialDistancing"));
        getServer().getPluginManager().addPermission(new Permission("socialdistancing.reload", "Allows that player to reload social distancing for the server by using /reloadSocialDistancing"));
        getCommand("toggleSocialDistancing").setExecutor(new ToggleCommand());
        getCommand("reloadSocialDistancing").setExecutor(new ReloadCommand());
    }

    @EventHandler
    public void pushPlayers(PlayerMoveEvent event) {
        if (enabled && !event.getPlayer().hasPermission("socialdistancing.disable")) {
            Player player = event.getPlayer();
            player.getNearbyEntities(distance, distance, distance).stream().filter(entity -> entity instanceof Player).forEach(otherPlayer -> {
                if (!otherPlayer.hasPermission("socialdistancing.disable")) {
                    Vector push = player.getLocation().toVector().subtract(otherPlayer.getLocation().toVector());
                    if (yCancelled) {
                        push.setY(0);
                    }
                    push = push.normalize().multiply(power);
                    player.setVelocity(push);
                    otherPlayer.setVelocity(push.multiply(-1));
                    if (sound != null) {
                        player.getWorld().playSound(player.getLocation(), sound, 1, 0);
                    }
                }
            });
        }

    }

    void loadConfig() {
        enabled = config.getBoolean("isEnabled");
        distance = config.getDouble("distance");
        power = config.getDouble("powerMultiplier");
        yCancelled = config.getBoolean("cancelYVelocity");
        String soundString = config.getString("sound");
        if (soundString.toUpperCase().equals("NONE")) {
            sound = null;
        } else {
            try {
                sound = Sound.valueOf(soundString);
            } catch (IllegalArgumentException e) {
                this.getLogger().severe("The sound supplied in config.yml is invalid! Defaulting to no sound");
                sound = null;
            }
        }

    }

    public class ToggleCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!sender.hasPermission("socialdistancing.toggle")) {
                sender.sendMessage(ChatColor.RED + "[SocialDistancing] You don't have permission!");
                return false;
            }
            config.set("isEnabled", !enabled);
            try {
                config.save(configFile);
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "[SocialDistancing] An error occured trying to modify the config file!");
                e.printStackTrace();
                return false;
            }
            loadConfig();
            sender.sendMessage(ChatColor.GREEN + "[SocialDistancing] Social Distancing has been " + (enabled ? "en" : "dis") + "abled.");
            return true;
        }
    }

    public class ReloadCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!sender.hasPermission("socialdistancing.reload")) {
                sender.sendMessage(ChatColor.RED + "[SocialDistancing] You don't have permission!");
                return false;
            }
            sender.sendMessage(ChatColor.GREEN + "[SocialDistancing] Social Distancing has been reloaded.");
            loadConfig();
            return true;
        }
    }
}
