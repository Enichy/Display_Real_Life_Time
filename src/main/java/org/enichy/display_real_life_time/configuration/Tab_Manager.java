package org.enichy.display_real_life_time.configuration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tab_Manager implements org.bukkit.command.TabCompleter{

    /**
     * Provides tab completion for the "date" command.
     * 
     * @param sender the command sender
     * @param command the command being executed
     * @param alias the command alias
     * @param args the command arguments
     * @return a list of tab completion suggestions
     */
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("date") && sender instanceof Player) {
            if (args.length == 1) {
                return IntStream.rangeClosed(-12, 14)
                        .mapToObj(Integer::toString)
                        .collect(Collectors.toList());
            }
        }
        return null;
    }
}
