package org.enichy.display_real_life_time.configuration;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;

/**
 * The Add_Change_Zone_Command class is responsible for handling the "/date" command,
 * which allows players to change their time zone offset and display the current date and time.
 * It implements the CommandExecutor interface and provides methods for managing player time zones,
 * updating the displayed time for all online players, and creating custom scoreboards.
 */
public class Add_Change_Zone_Command implements CommandExecutor {
    private HashMap<UUID, Integer> playerUtcOffsets = new HashMap<>();

    private Add_Change_Zone_Command() {}

    private static class SingletonHelper {
        private static final Add_Change_Zone_Command INSTANCE = new Add_Change_Zone_Command();
    }

    public static Add_Change_Zone_Command getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Executes the command when it is triggered by a player.
     *
     * @param sender the command sender
     * @param command the command that was executed
     * @param label the alias or label used to execute the command
     * @param args the arguments provided with the command
     * @return true if the command was executed successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("date") && sender instanceof Player) {
            Player player = (Player) sender;
            // Add the player's UUID to the HashMap with a default UTC offset of 0
            playerUtcOffsets.put(player.getUniqueId(), 0);
            handleDateCommand(player, args);
        }
        return true;
    }

    /**
     * Handles the date command by updating the UTC offset for the player and displaying the updated date and time.
     * 
     * @param player the player who executed the command
     * @param args   the command arguments
     */
    private void handleDateCommand(Player player, String[] args) {
        if (args.length == 1) {
            try {
                int utcOffset = parseUtcOffset(args[0]);
                if (utcOffset >= -12 && utcOffset <= 14) {
                    // Store the UTC offset for the player
                    playerUtcOffsets.put(player.getUniqueId(), utcOffset);
                    displayDateTime(player, utcOffset);
                    player.sendMessage(ChatColor.GREEN.toString() + "[✓]・The date and time have been updated.");
                } else {
                    player.sendMessage(ChatColor.RED.toString() + "[⊗]・Invalid UTC offset. Please enter a number between -12 and 14.");
                }
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED.toString() + "[⊗]・Invalid UTC offset. Please enter a number.");
            }
        } else {
            player.sendMessage(ChatColor.RED.toString() + "[⊗]・Please specify a UTC offset.");
        }
    }

    /**
     * Parses the given string argument as an integer representing the UTC offset.
     * 
     * @param arg the string argument to be parsed
     * @return the parsed UTC offset as an integer
     * @throws NumberFormatException if the string argument cannot be parsed as an integer
     */
    public int parseUtcOffset(String arg) throws NumberFormatException {
        return Integer.parseInt(arg);
    }

    /**
     * Displays the current date and time for a player with the specified UTC offset.
     *
     * @param player    the player to display the date and time for
     * @param utcOffset the UTC offset in hours
     */
    private void displayDateTime(Player player, int utcOffset) {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneOffset.ofHours(utcOffset));
        String formattedDate = formatDateTime(dateTime, "yyyy-MM-dd");
        String formattedTime = formatDateTime(dateTime, "HH:mm:ss");

        Scoreboard scoreboard = createScoreboard(utcOffset, formattedDate, formattedTime);
        player.setScoreboard(scoreboard);
    }
    
    /**
     * Formats the given ZonedDateTime object into a string representation using the specified pattern.
     *
     * @param dateTime the ZonedDateTime object to be formatted
     * @param pattern the pattern to be used for formatting
     * @return the formatted string representation of the ZonedDateTime object
     */
    private String formatDateTime(ZonedDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Creates a new scoreboard with the given UTC offset, date, and time.
     *
     * @param utcOffset the UTC offset for the scoreboard
     * @param date the date to be displayed on the scoreboard
     * @param time the time to be displayed on the scoreboard
     * @return the created scoreboard
     */
    private Scoreboard createScoreboard(int utcOffset, String date, String time) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("date", Criteria.DUMMY, ("Date & Time (UTC" + utcOffset + ")"));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score timeScore = objective.getScore("Time: " + time);
        timeScore.setScore(1);
        Score dateScore = objective.getScore("Date: " + date);
        dateScore.setScore(0);

        return scoreboard;
    }
    
    /**
     * Retrieves the UTC offset for the specified player.
     * If the player's UTC offset is not found, returns 0.
     *
     * @param player the player for which to retrieve the UTC offset
     * @return the UTC offset of the player, or 0 if not found
     */
    public int getUtcOffset(Player player) {
        return playerUtcOffsets.getOrDefault(player.getUniqueId(), 0);
    }

    /**
     * Checks if the player has a custom scoreboard.
     * 
     * @param player the player to check
     * @return true if the player has a custom scoreboard, false otherwise
     */
    public boolean hasCustomScoreboard(Player player) {
        Scoreboard playerScoreboard = player.getScoreboard();
        Scoreboard defaultScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        return playerScoreboard != defaultScoreboard;
    }

    
    /**
     * Changes the dynamically displayed time for all online players.
     * This method is scheduled to run repeatedly at a fixed interval.
     * The time is updated based on the UTC offset of each player.
     *
     * @param plugin the plugin instance
     */
    public void changeDynamicallyTime(Plugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (hasCustomScoreboard(player)){
                    if (playerUtcOffsets.containsKey(player.getUniqueId())) {
                        plugin.getLogger().info("Updating time for player: " + player.getUniqueId());
                        int utcOffset = getUtcOffset(player);
                        displayDateTime(player, utcOffset);
                        // displayPlayerUtcOffsets(plugin);
                    }
                }
            }
            }
        }, 0L, 20L); // 20 ticks = 1 second
    }

    /**
     * Displays the UTC offsets of all players.
     * 
     * @param plugin the plugin instance
     */
    public void displayPlayerUtcOffsets(Plugin plugin) {
        for (Map.Entry<UUID, Integer> entry : playerUtcOffsets.entrySet()) {
            plugin.getLogger().info("Player: " + entry.getKey() + ", UTC Offset: " + entry.getValue());
        }
        plugin.getLogger().info("===================================");
    }

    /**
     * Removes the player's UUID from the playerUtcOffsets map.
     * 
     * @param playerId the UUID of the player to be removed
     */
    public void removePlayer(UUID playerId) {
        playerUtcOffsets.remove(playerId);
    }
}
