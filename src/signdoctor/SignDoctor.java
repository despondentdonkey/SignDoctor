package signdoctor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import signdoctor.commands.*;

public class SignDoctor extends JavaPlugin {
    public static Plugin plugin;

    //Metadata keys
    public static final String SIGN_EDIT = "SignDoctor_editSign";
    public static final String SIGN_MULTI_EDIT = "SignDoctor_editMultiSign";
    public static final String SIGN = "SignDoctor_activeSign";
    public static final String SIGNS = "SignDoctor_activeSigns";
    public static final String SIGN_LINES = "SignDoctor_signLinesArray";
    public static final String PREV_LOCATION = "SignDoctor_previousLocation";
    public static final String SIGN_LINE = "SignDoctor_signLine";

    //Messages
    public static final String MSG_EDIT_DISABLED = "Sign editing is not enabled. To enable, use the command: toggleSignEdit";
    public static final String MSG_NO_ACTIVE_SIGN = "No sign is active.";

    public static final String PERM_EDIT = "signdoctor.signediting";
    public static final String PERM_TELEPORT = "signdoctor.teleporting";

    public static Configuration config;

    public static boolean noSelector = false;

    @Override
    public void onEnable() {
        plugin = this;

        config = new Configuration(this);

        noSelector = (config.selectorItem.isEmpty() || config.selectorItem.equalsIgnoreCase("NULL"));

        getServer().getPluginManager().registerEvents(new SignDoctorEvents(), this);

        getCommand("editSign").setExecutor(new CommandEditSign());
        getCommand("editSignln").setExecutor(new CommandEditSignLine());
        getCommand("appendToSign").setExecutor(new CommandAppendToSign(false));
        getCommand("prependToSign").setExecutor(new CommandAppendToSign(true));
        getCommand("replaceSign").setExecutor(new CommandReplaceSign(false));
        getCommand("replaceSignAll").setExecutor(new CommandReplaceSign(true));
        getCommand("replaceln").setExecutor(new CommandReplaceLine(false));
        getCommand("replacelnAll").setExecutor(new CommandReplaceLine(true));
        getCommand("switchln").setExecutor(new CommandSwitchLine());
        getCommand("clearSign").setExecutor(new CommandClearPaste(false, true));
        getCommand("pasteSign").setExecutor(new CommandClearPaste(true, true));
        getCommand("clearln").setExecutor(new CommandClearPaste(false, false));
        getCommand("pasteln").setExecutor(new CommandClearPaste(true, false));
        getCommand("copySign").setExecutor(new CommandCopyCut(false, true));
        getCommand("cutSign").setExecutor(new CommandCopyCut(true, true));
        getCommand("copyln").setExecutor(new CommandCopyCut(false, false));
        getCommand("cutln").setExecutor(new CommandCopyCut(true, false));
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Sign sign = (Sign) getMetadata(p, SignDoctor.SIGN, this);

            if (!p.hasPermission(PERM_EDIT)) {
                say(p, "You do not have permission to edit signs.");
                return true;
            }

            //tpToSign command
            if (cmd.getName().equalsIgnoreCase("tpToSign")) {
                if (!p.hasPermission(PERM_TELEPORT)) {
                    say(p, "You do not have permission to teleport.");
                    return true;
                }

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
                if (!p.hasPermission(PERM_TELEPORT)) {
                    say(p, "You do not have permission to teleport.");
                    return true;
                }

                Location previousLocation = (Location) getMetadata(p, PREV_LOCATION, this);

                if (previousLocation != null) {
                    p.teleport(previousLocation);
                } else {
                    say(p, "No location has been logged. You must first use 'tpToSign'.");
                }

                return true;
            }

            //Toggle sign edit command.
            if (cmd.getName().equalsIgnoreCase("toggleSignEdit")) {
                boolean editing = isEditing(p);
                setEditing(p, !editing);
                say(p, !editing ? "Editing has been enabled." : "Editing has been disabled.");
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("toggleMultiSignEdit")) {
                setMultiEditing(p, !isMultiEditing(p));
                if (isMultiEditing(p)) {
                    setEditing(p, true); // Enabling multi editing should enable normal sign editing as well.
                    say(p, "Multiple editing has been enabled.");
                } else {
                    say(p, "Multiple editing has been disabled.");
                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("deactivateSigns")) {
                List<Sign> signs = new ArrayList<Sign>();
                p.setMetadata(SIGNS, new FixedMetadataValue(plugin, signs));
                return true;
            }

        } else {
            sender.sendMessage("Sign Doctor: You must be a player for this to work.");
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

    public static void setMultiEditing(Player p, boolean val) {
        p.setMetadata(SIGN_MULTI_EDIT, new FixedMetadataValue(plugin, val));
    }

    public static boolean isMultiEditing(Player p) {
        return (boolean) getMetadata(p, SIGN_MULTI_EDIT, plugin);
    }

    @SuppressWarnings("unchecked")
    public static List<Sign> getActiveSigns(Player p) {
        return (List<Sign>) SignDoctor.getMetadata(p, SIGNS, plugin);
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
     * Copies a sign's line and stores it in the player's clipboard. (The player has a separate clipboard for lines.)
     * 
     * @param p
     * @param s
     * @param line
     * @throws InvalidLineException
     */
    public static void copyLine(Player p, Sign s, int line) throws InvalidLineException {
        if (isValidLine(line)) {
            p.setMetadata(SIGN_LINE, new FixedMetadataValue(plugin, s.getLine(line)));
        } else {
            throw new InvalidLineException();
        }
    }

    public static void clearLine(Sign s, int line) throws InvalidLineException {
        if (isValidLine(line)) {
            s.setLine(line, "");
        } else {
            throw new InvalidLineException();
        }
    }

    public static void pasteLine(Sign s, String text, int line) throws InvalidLineException {
        if (isValidLine(line)) {
            s.setLine(line, text);
        } else {
            throw new InvalidLineException();
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
            sb.append(args[i]);

            //Add a space if it is not the last element.
            if (i < args.length - 1)
                sb.append(" ");
        }

        return sb.toString().replaceAll(config.spacingStr, " ");
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
        p.sendMessage(ChatColor.GOLD + "[Sign Doctor] " + ChatColor.WHITE + s);
    }

    public static void editSign(Sign s, String lines[]) {
        for (int i = 0; i < Math.min(lines.length, 4); ++i) {
            if (!lines[i].equals(config.blankStr)) {
                String text = lines[i].replaceAll(config.spacingStr, " ");
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

    public static void appendToSign(Sign s, int line, String args[]) throws InvalidLineException {
        if (isValidLine(line)) {
            String lineText = s.getLine(line);
            String appendText = mergeStrings(args);

            s.setLine(line, lineText + appendText);
        } else {
            throw new InvalidLineException();
        }
    }

    public static void prependToSign(Sign s, int line, String args[]) throws InvalidLineException {
        if (isValidLine(line)) {
            String lineText = s.getLine(line);
            String prependText = mergeStrings(args);

            s.setLine(line, prependText + lineText);
        } else {
            throw new InvalidLineException();
        }
    }

    public static void replaceln(Sign s, int line, String regex, String replacement, boolean replaceAll) throws InvalidLineException, PatternSyntaxException {
        if (isValidLine(line)) {
            String lineText = s.getLine(line);
            replacement = replacement.replaceAll(config.spacingStr, " ");

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
