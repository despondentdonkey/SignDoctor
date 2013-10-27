package main;

import java.util.*;
import java.util.regex.*;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.metadata.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

/**
 * Sign Editor Plugin Class.
 * 
 * @author Parker Miller
 * 
 */
public class SignEditor extends JavaPlugin {
    public static Plugin plugin;

    //Metadata keys
    public static final String SIGN_EDIT = "SignEditor_editSign";
    public static final String SIGN = "SignEditor_activeSign";
    public static final String SIGN_LINES = "SignEditor_signLinesArray";
    public static final String PREV_LOCATION = "SignEditor_previousLocation";

    //Messages
    public static final String MSG_EDIT_DISABLED = "Sign editing is not enabled. To enable, use the command: toggleSignEdit";
    public static final String MSG_NO_ACTIVE_SIGN = "No sign is active.";

    public static final String PERM_EDIT = "signediting";

    public static FileConfiguration config;
    public static boolean enableEditing = false;
    public static String spacingStr = "_";
    public static String blankStr = "\\n";
    public static String selectorItem = "FEATHER";

    public static boolean noSelector = false;

    @Override
    public void onEnable() {
        plugin = this;

        config = this.getConfig();
        enableEditing = config.getBoolean("enableEditingByDefault");
        spacingStr = config.getString("spacingStr");
        blankStr = config.getString("blankStr");
        selectorItem = config.getString("selectorItem").toUpperCase();

        noSelector = (selectorItem.isEmpty() || selectorItem.equalsIgnoreCase("NULL"));

        getServer().getPluginManager().registerEvents(new SignEditorEvents(), this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            boolean editing = (boolean) getMetadata(p, SIGN_EDIT, this);
            Sign sign = (Sign) getMetadata(p, SignEditor.SIGN, this);

            if (!p.hasPermission(PERM_EDIT)) {
                say(p, "You do not have permission to edit signs.");
                return true;
            }

            //tpToSign command
            if (cmd.getName().equalsIgnoreCase("tpToSign")) {
                if (sign != null) {
                    p.setMetadata(PREV_LOCATION, new FixedMetadataValue(this, p.getLocation()));
                    p.teleport(sign.getLocation());
                } else {
                    say(p, MSG_NO_ACTIVE_SIGN);
                }

                return true;
            }

            //tpBackFromSign command
            if (cmd.getName().equalsIgnoreCase("tpBackFromSign")) {
                Location previousLocation = (Location) getMetadata(p, PREV_LOCATION, this);

                if (previousLocation != null) {
                    p.teleport(previousLocation);
                } else {
                    say(p, "No location has been logged. You must first use 'tpToSign'.");
                }

                return true;
            }

            if (editing) {
                if (sign != null) {
                    //Toggle sign edit command.
                    if (cmd.getName().equalsIgnoreCase("toggleSignEdit")) {
                        setEditing(p, !editing);
                        say(p, !editing ? "Editing has been enabled." : "Editing has been disabled.");

                        return true;
                    }

                    //Edit sign command.
                    if (cmd.getName().equalsIgnoreCase("editSign")) {
                        editSign(sign, args);
                        updateSign(p, sign);
                        return true;
                    }

                    //Edit sign line command.
                    if (cmd.getName().equalsIgnoreCase("editSignln")) {
                        if (args.length < 1)
                            return false;

                        try {
                            int line = Integer.parseInt(args[0]) - 1;
                            editSignln(sign, line, Arrays.copyOfRange(args, 1, args.length));
                            updateSign(p, sign);
                            return true;
                        } catch (NumberFormatException e) {
                            return false;
                        } catch (InvalidLineException e) {
                            say(p, e.getMessage());
                            return true;
                        }
                    }

                    //appendToSign command
                    if (cmd.getName().equalsIgnoreCase("appendToSign")) {
                        if (args.length < 2)
                            return false;

                        try {
                            int line = Integer.parseInt(args[0]) - 1;
                            appendToSign(p, sign, line, Arrays.copyOfRange(args, 1, args.length));
                            updateSign(p, sign);
                            return true;
                        } catch (NumberFormatException e) {
                            return false;
                        } catch (InvalidLineException e) {
                            say(p, e.getMessage());
                            return true;
                        }
                    }

                    //replaceln command
                    if (cmd.getName().equalsIgnoreCase("replaceln")) {
                        if (args.length < 3)
                            return false;

                        int line = 0;
                        boolean replaceAll = false;

                        try {
                            line = Integer.parseInt(args[0]) - 1;
                            if (args.length >= 4) {
                                replaceAll = Boolean.parseBoolean(args[3]);
                            }
                        } catch (NumberFormatException e) {
                            return false;
                        }

                        try {
                            replaceln(sign, line, args[1], args[2], replaceAll);
                        } catch (PatternSyntaxException e) {
                            say(p, "Regex syntax is incorrect.");
                            return true;
                        } catch (InvalidLineException e) {
                            say(p, e.getMessage());
                            return true;
                        }

                        updateSign(p, sign);
                        return true;
                    }

                    //switchln command
                    if (cmd.getName().equalsIgnoreCase("switchln")) {
                        if (args.length < 2) {
                            return false;
                        }

                        try {
                            int lineTargetNum = Integer.parseInt(args[0]) - 1;
                            int lineDestNum = Integer.parseInt(args[1]) - 1;
                            switchln(sign, lineTargetNum, lineDestNum);
                            updateSign(p, sign);
                            return true;
                        } catch (NumberFormatException e) {
                            return false;
                        } catch (InvalidLineException e) {
                            say(p, e.getMessage());
                            return true;
                        }
                    }

                    //Clear sign command.
                    if (cmd.getName().equalsIgnoreCase("clearSign")) {
                        clearSign(sign);
                        updateSign(p, sign);
                        return true;
                    }

                    //Copy sign command.
                    if (cmd.getName().equalsIgnoreCase("copySign")) {
                        copySign(p, sign);
                        say(p, "Sign has been copied.");
                        return true;
                    }

                    //Cut sign command.
                    if (cmd.getName().equalsIgnoreCase("cutSign")) {
                        copySign(p, sign);
                        clearSign(sign);
                        updateSign(p, sign);

                        say(p, "Sign has been cut.");
                        return true;
                    }

                    //Paste sign command.
                    if (cmd.getName().equalsIgnoreCase("pasteSign")) {
                        pasteSign(sign, (String[]) getMetadata(p, SIGN_LINES, this));
                        updateSign(p, sign);
                        return true;
                    }
                } else {
                    say(p, MSG_NO_ACTIVE_SIGN);
                    return true;
                }
            } else {
                say(p, MSG_EDIT_DISABLED);
                return true;
            }
        } else {
            sender.sendMessage("Sign Editor: You must be a player for this to work.");
            return true;
        }

        return false;
    }

    public static void setEditing(Player p, boolean val) {
        p.setMetadata(SIGN_EDIT, new FixedMetadataValue(plugin, val));
    }

    public static boolean isEditing(Player p) {
        return (boolean) getMetadata(p, SIGN_EDIT, plugin);
    }

    /**
     * Copies a sign and stores it into the player's clipboard.
     * 
     * @param p
     * @param s
     */
    public static void copySign(Player p, Sign s) {
        String[] sl = new String[4];

        for (int i = 0; i < sl.length; i++) {
            sl[i] = s.getLine(i);
        }

        p.setMetadata(SIGN_LINES, new FixedMetadataValue(plugin, sl));
    }

    public static void clearSign(Sign s) {
        for (int i = 0; i < 4; ++i) {
            s.setLine(i, "");
        }
    }

    public static void pasteSign(Sign s, String lines[]) {
        for (int i = 0; i < lines.length; i++) {
            s.setLine(i, lines[i]);
        }
    }

    /**
     * Checks if a integer is a valid line number. (0-3)
     * 
     * @param line The line number you want to check, starting from 0 ending at 3.
     * @return
     */
    public static boolean isValidLine(int line) {
        return (line >= 0 && line <= 3);
    }

    /**
     * Merges an array of strings into one string. It also formats it to use spacingStr and spaces after each element.
     * 
     * @param args
     * @return The merged string
     */
    public static String mergeStrings(String[] args) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < args.length; ++i) {
            Bukkit.getServer().getLogger().info(args[i]);
            sb.append(args[i]);

            //Add a space if it is not the last element.
            if (i < args.length - 1)
                sb.append(" ");
        }

        return sb.toString().replaceAll(spacingStr, " ");
    }

    /**
     * Updates a sign and calls a SignChangeEvent.
     * 
     * @param p The player who updated the sign.
     * @param s The sign you want to update.
     */
    public static void updateSign(Player p, Sign s) {
        //The first update is used to change the text of the sign just in case the SignChangeEvent blocks it. This is used mostly to support Lockette.
        s.update();

        Bukkit.getServer().getPluginManager().callEvent(new SignChangeEvent(s.getBlock(), p, s.getLines()));
        //We must update again after the SignChangeEvent for colors to work. This also allows other plugins to be compatible.
        s.update();
    }

    public static void say(Player p, String s) {
        p.sendMessage(ChatColor.GOLD + "[Sign Editor] " + ChatColor.WHITE + s);
    }

    public static void editSign(Sign s, String lines[]) {
        for (int i = 0; i < Math.min(lines.length, 4); ++i) {
            if (!lines[i].equals(blankStr)) {
                String text = lines[i].replaceAll(spacingStr, " ");
                s.setLine(i, text);
            }
        }
    }

    public static void editSignln(Sign s, int line, String args[]) throws InvalidLineException {
        if (isValidLine(line)) {
            String mergedStr = mergeStrings(args);
            s.setLine(line, mergedStr);
        } else {
            throw new InvalidLineException();
        }
    }

    public static void appendToSign(Player p, Sign s, int line, String args[]) throws InvalidLineException {
        if (isValidLine(line)) {
            String lineText = s.getLine(line);
            String appendText = mergeStrings(Arrays.copyOfRange(args, 1, args.length));

            s.setLine(line, lineText + appendText);
        } else {
            throw new InvalidLineException();
        }
    }

    public static void replaceln(Sign s, int line, String regex, String replacement, boolean replaceAll) throws InvalidLineException, PatternSyntaxException {
        if (isValidLine(line)) {
            String lineText = s.getLine(line);
            regex = regex.replaceAll(spacingStr, " ");

            if (replaceAll) {
                lineText = lineText.replaceAll(regex, replacement);
            } else {
                lineText = lineText.replaceFirst(regex, replacement);
            }

            s.setLine(line, lineText);
        } else {
            throw new InvalidLineException();
        }
    }

    public static void switchln(Sign s, int lineTargetNum, int lineDestNum) throws InvalidLineException {
        if (isValidLine(lineTargetNum) && isValidLine(lineDestNum)) {
            String lineTarget = s.getLine(lineTargetNum);
            String lineDest = s.getLine(lineDestNum);

            s.setLine(lineDestNum, lineTarget);
            s.setLine(lineTargetNum, lineDest);
        } else {
            throw new InvalidLineException();
        }
    }

    public static Object getMetadata(Player player, String key, Plugin plugin) {
        List<MetadataValue> values = player.getMetadata(key);
        for (MetadataValue value : values) {
            if (value.getOwningPlugin().getDescription().getName().equals(plugin.getDescription().getName())) {
                return value.value();
            }
        }
        return null;
    }
}
