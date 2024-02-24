package org.enichy.display_real_life_time;

import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.enichy.display_real_life_time.configuration.Add_Change_Zone_Command;
import org.enichy.display_real_life_time.configuration.Disable_Time_Command;
import org.enichy.display_real_life_time.configuration.Tab_Manager;

/**
 * The main class of the DisplayRealLifeTime plugin.
 * This plugin displays the real-life time in Minecraft.
 */
public final class DisplayRealLifeTime extends JavaPlugin {

    private Add_Change_Zone_Command addChangeZoneCommand;

    
    /**
     * Called when the plugin is enabled.
     * This method initializes the plugin, registers commands and tabs,
     * and calls the changeDynamicallyTime method of the Add_Change_Zone_Command class.
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("DisplayRealLifeTime has been enabled.");
        // Register all the commands
        registerCommands();
        // Register all the tabs
        registerTabs();
        
        // Create a new instance of the Add_Change_Zone_Command class
        addChangeZoneCommand = Add_Change_Zone_Command.getInstance();
        // Call the changeDynamicallyTime method
        addChangeZoneCommand.changeDynamicallyTime(this);
    }

    /**
     * Called when the plugin is disabled.
     * This method performs cleanup and shutdown logic.
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("DisplayRealLifeTime has been disabled.");
    }

    /**
     * Registers the commands for the plugin.
     * @return true if the commands are successfully registered, false otherwise.
     */
    public boolean registerCommands() {
        this.getCommand("date").setExecutor(Add_Change_Zone_Command.getInstance());
        this.getCommand("clear_scoreboard").setExecutor(new Disable_Time_Command());
        return true;
    }

    /**
     * Registers the tabs for the "date" command.
     */
    public void registerTabs() {
        this.getCommand("date").setTabCompleter((TabCompleter) new Tab_Manager());
    }
}