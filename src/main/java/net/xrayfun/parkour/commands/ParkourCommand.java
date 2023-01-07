package net.xrayfun.parkour.commands;

import net.xrayfun.parkour.ParkourManager;
import net.xrayfun.parkour.data.ParkourSettings;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ParkourCommand implements CommandExecutor {
    private static boolean tooManyArgs(Player player, int arg, boolean msg, int... lim) {
        if (lim.length == 1) { // фиксированное кол-во аргументов в лимите
            if (arg > lim[0] && msg)
                player.sendMessage("Слишком много аргументов");
            if (arg < lim[0] && msg)
                player.sendMessage("Слишком мало аргументов");
            return arg > lim[0] || arg < lim[0];
        } else if (lim.length == 2) { // диапазон кол-ва аргументов в лимите
            if (lim[0] > arg) {
                if (msg) player.sendMessage("Недостаточно аргументов");
                return true;
            } else if (lim[1] < arg) {
                if (msg) player.sendMessage("Слишком много аргументов");
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private static boolean tooManyArgs(Player player, int arg, int... lim) {
        return tooManyArgs(player, arg, true, lim);
    }

    private static void sendHelp(Player player) {
        player.sendMessage(
                " §r§7*§f /parkour [...]\n" +
                        " §r§7*     §f help §r§7- Shows information about the commands\n" +
                        " §r§7*     §f leave §r§7- Leave the current parkour (in game)\n" +
                        " §r§7*     §f reset §r§7- Reset the current parkour (in game)");
        if (player.hasPermission("parkour.admin"))
            player.sendMessage(
                    " §r§7*     §f create §a[name] §d<type> §r§7- Create new parkour in current world with type (§dinfinite/hard§r§7)\n" +
                            " §r§7*     §f remove §a[name] §r§7- Remove current parkour from the world\n" +
                            " §r§7*     §f pos1 §r§7- Sets the first parkour position\n" +
                            " §r§7*     §f pos2 §r§7- Sets the second parkour position\n" +
                            " §r§7*     §f respawn §r§7- Sets the restart point for parkour (only hard type)");
    }

    private static final List<String> playerCommands = Arrays.asList("start", "leave", "reset", "help");
    private static final List<String> adminCommands = Arrays.asList("create", "remove", "pos1", "pos2", "respawn", "difficult");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        Location loc = player.getLocation();
        if (!player.hasPermission("parkour.base")) {
            player.sendMessage("Недостаточно прав!");
            return true;
        }
        if (args.length == 0) {
//            Gui_inProcess.openInventory(player);
            sendHelp(player);
            return true;
        }
        if (playerCommands.contains(args[0])) {
            if (args[0].equals("start")) {
                if (tooManyArgs(player, args.length, 2)) return false;
                ParkourManager.startSession(player, args[1]);
            }
            if (args[0].equals("leave")) {
                if (tooManyArgs(player, args.length, 1)) return false;
                ParkourManager.stopSession(true, player);
            }
            if (args[0].equals("reset")) {
                if (tooManyArgs(player, args.length, 1)) return false;
                ParkourManager.restartSession((Player) sender);
            }
            if (args[0].equals("help"))
                sendHelp(player);
            return true;
        } else if (adminCommands.contains(args[0])) {
            if (!player.hasPermission("parkour.admin")) {
                player.sendMessage("Недостаточно прав!");
                return true;
            }
            if (args[0].equals("create")) {
                if (tooManyArgs(player, args.length, 3)) return false;
                try {
                    if (!ParkourManager.parkourSettings.containsKey(args[1])) {
                        ParkourSettings.Difficult difficult = ParkourSettings.Difficult.valueOf(args[2].toUpperCase());
                        ParkourManager.makeSettings(args[1], difficult, loc, loc, loc);
                        player.sendMessage("Паркур " + args[1] + " успешно создан.\n" +
                                "Укажите позиции паркура и место возраждения!\n" +
                                "* По умолчанию устанавливается ваша позиция!");
                    } else {
                        player.sendMessage("Такой паркур уже существует!");
                    }
                } catch (Exception e) {
                    player.sendMessage("Тип указан неверно!");
                }
            }
            if (args[0].equals("remove")) {
                if (tooManyArgs(player, args.length, 2)) return false;
                if (!ParkourManager.parkourSettings.containsKey(args[1])) {
                    player.sendMessage("Такого паркура не существует.");
                    return false;
                }
                ParkourManager.removeSettings(args[1]);
                player.sendMessage("Паркур " + args[1] + " успешно удален.");
            }
            if (args[0].equals("pos1")) {
                if (tooManyArgs(player, args.length, 2)) return false;
                if (!ParkourManager.parkourSettings.containsKey(args[1])) {
                    player.sendMessage("Такого паркура не существует.");
                    return false;
                }
                ParkourSettings settings = ParkourManager.getSettings(args[1]);
                settings.setPos1(loc);
                player.sendMessage("Первая позиция области паркура " + args[1] + "");
            }
            if (args[0].equals("pos2")) {
                if (tooManyArgs(player, args.length, 2)) return false;
                if (!ParkourManager.parkourSettings.containsKey(args[1])) {
                    player.sendMessage("Такого паркура не существует.");
                    return false;
                }
                ParkourSettings settings = ParkourManager.getSettings(args[1]);
                settings.setPos2(loc);
            }
            if (args[0].equals("respawn")) {
                if (tooManyArgs(player, args.length, 2)) return false;
                if (!ParkourManager.parkourSettings.containsKey(args[1])) {
                    player.sendMessage("Такого паркура не существует.");
                    return false;
                }
                ParkourSettings settings = ParkourManager.getSettings(args[1]);
                settings.setRespawn(loc);
            }
            if (args[0].equals("difficult")) {
                if (tooManyArgs(player, args.length, 3)) return false;
                if (!ParkourManager.parkourSettings.containsKey(args[1])) {
                    player.sendMessage("Такого паркура не существует.");
                    return false;
                }
                ParkourSettings settings = ParkourManager.getSettings(args[1]);
                settings.setDifficult(ParkourSettings.Difficult.valueOf(args[2].toUpperCase()));
            }
            return true;
        } else {
            player.sendMessage("Неверные аргументы!");
            sendHelp(player);
            return true;
        }
    }
}
