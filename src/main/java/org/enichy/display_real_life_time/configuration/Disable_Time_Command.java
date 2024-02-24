package org.enichy.display_real_life_time.configuration;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;

/**
 * This class represents a command executor for the "clear_scoreboard" command.
 * It clears the scoreboard of the player who executes the command.
 */
public class Disable_Time_Command implements CommandExecutor {

    /**
     * Executes the command when it is triggered by a player.
     *
     * @param sender the command sender
     * @param command the command that was executed
     * @param label the alias used for the command
     * @param args the arguments provided with the command
     * @return true if the command was executed successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("clear_scoreboard")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                Scoreboard blankScoreboard = manager.getNewScoreboard();

                // Remove all objectives from the scoreboard
                for (Objective objective : blankScoreboard.getObjectives()) {
                    objective.unregister();
                }

                player.setScoreboard(blankScoreboard);
                player.sendMessage(ChatColor.GREEN.toString() + "[✓]・Your scoreboard has been cleared.");

                // Remove the player from the HashMap
                Add_Change_Zone_Command.getInstance().removePlayer(player.getUniqueId());
            } else {
                JavaPlugin.getPlugin(JavaPlugin.class).getLogger().info("This command can only be executed by a player.");
            }
        }
        return true;
    }
}
