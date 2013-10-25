package main;

import java.util.*;
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
    public static final String MSG_INVALID_LINE_NUM = "Invalid line number. Must be 1 to 4.";

    public static final String PERM_EDIT = "signediting";

    public static FileConfiguration config;
    public static boolean enableEditing = false;
    public static String spacingStr = "_";
    public static String newlineStr = "\\n";

    @Override
    public void onEnable() {
        plugin = this;

        config = this.getConfig();
        enableEditing = config.getBoolean("enableEditingByDefault");
        spacingStr = config.getString("spacingStr");
        newlineStr = config.getString("newlineStr");

        //Register events.
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

            if (cmd.getName().equalsIgnoreCase("tpToSign")) {
                if (sign != null) {
                    p.setMetadata(PREV_LOCATION, new FixedMetadataValue(this, p.getLocation()));
                    p.teleport(sign.getLocation());
                } else {
                    say(p, MSG_NO_ACTIVE_SIGN);
                }

                return true;
            }

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
                        p.setMetadata(SIGN_EDIT, new FixedMetadataValue(this, !editing));
                        say(p, !editing ? "Editing has been enabled." : "Editing has been disabled.");

                        return true;
                    }

                    //Edit sign command.
                    if (cmd.getName().equalsIgnoreCase("editSign")) {
                        for (int i = 0; i < Math.min(args.length, 4); i++) {
                            say(p, args[i]);
                            if (!args[i].equals(newlineStr)) {
                                String text = args[i].replaceAll(spacingStr, " ");
                                sign.setLine(i, text);
                            }
                        }

                        updateSign(p, sign);

                        return true;
                    }

                    //Edit sign line command.
                    if (cmd.getName().equalsIgnoreCase("editSignln")) {
                        int line = 0;

                        if (args.length < 1) {
                            return false;
                        }

                        try {
                            line = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            return false;
                        }

                        if (line > 0 && line < 5) {
                            StringBuilder sb = new StringBuilder();

                            for (int i = 1; i < args.length; ++i) {
                                sb.append(args[i]);

                                //Add a space if it is not the last element.
                                if (i < args.length - 1)
                                    sb.append(" ");
                            }

                            sign.setLine(line - 1, sb.toString());

                            updateSign(p, sign);
                        } else {
                            say(p, MSG_INVALID_LINE_NUM);
                        }

                        return true;
                    }

                    if (cmd.getName().equalsIgnoreCase("TESTappendToSign")) {
                        int lnNum = Integer.parseInt(args[0]) - 1;
                        String ln = sign.getLine(lnNum);

                        StringBuilder sb = new StringBuilder(ln);
                        sb.append(args[1]);

                        sign.setLine(lnNum, sb.toString());

                        updateSign(p, sign);
                    }

                    if (cmd.getName().equalsIgnoreCase("switchln")) {
                        if (args.length < 2) {
                            return false;
                        }

                        int lineTargetNum = 0;
                        int lineDestNum = 0;

                        try {
                            lineTargetNum = Integer.parseInt(args[0]) - 1;
                            lineDestNum = Integer.parseInt(args[1]) - 1;
                        } catch (NumberFormatException e) {
                            return false;
                        }

                        if ((lineTargetNum >= 0 && lineTargetNum <= 3) && (lineDestNum >= 0 && lineDestNum <= 3)) {
                            String lineTarget = sign.getLine(lineTargetNum);
                            String lineDest = sign.getLine(lineDestNum);

                            sign.setLine(lineDestNum, lineTarget);
                            sign.setLine(lineTargetNum, lineDest);

                            updateSign(p, sign);
                        } else {
                            say(p, MSG_INVALID_LINE_NUM);
                        }

                        return true;
                    }

                    //Clear sign command.
                    if (cmd.getName().equalsIgnoreCase("clearSign")) {
                        clearActiveSign(p);
                        return true;
                    }

                    //Copy sign command.
                    if (cmd.getName().equalsIgnoreCase("copySign")) {
                        if (copyActiveSign(p)) {
                            say(p, "Sign has been copied.");
                        }

                        return true;
                    }

                    //Cut sign command.
                    if (cmd.getName().equalsIgnoreCase("cutSign")) {
                        if (copyActiveSign(p)) {
                            clearActiveSign(p);
                            say(p, "Sign has been cut.");
                        }

                        return true;
                    }

                    //Paste sign command.
                    if (cmd.getName().equalsIgnoreCase("pasteSign")) {
                        String[] sl = (String[]) getMetadata(p, SIGN_LINES, this);

                        for (int i = 0; i < sl.length; i++) {
                            sign.setLine(i, sl[i]);
                        }

                        sign.update();
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

    public boolean copyActiveSign(Player p) {
        boolean editing = (boolean) getMetadata(p, SIGN_EDIT, this);
        Sign sign = (Sign) getMetadata(p, SignEditor.SIGN, this);

        if (editing) {
            if (sign != null) {
                String[] sl = new String[4];

                for (int i = 0; i < sl.length; i++) {
                    sl[i] = sign.getLine(i);
                }

                p.setMetadata(SIGN_LINES, new FixedMetadataValue(this, sl));

                return true;
            } else {
                say(p, MSG_NO_ACTIVE_SIGN);
                return false;
            }
        } else {
            say(p, MSG_EDIT_DISABLED);
            return false;
        }
    }

    public boolean clearActiveSign(Player p) {
        boolean editing = (boolean) getMetadata(p, SIGN_EDIT, this);
        Sign sign = (Sign) getMetadata(p, SignEditor.SIGN, this);

        if (editing) {
            if (sign != null) {
                for (int i = 0; i < 4; ++i) {
                    sign.setLine(i, "");
                }

                sign.update();

                return true;
            } else {
                say(p, MSG_NO_ACTIVE_SIGN);
                return false;
            }
        } else {
            say(p, MSG_EDIT_DISABLED);
            return false;
        }
    }

    /**
     * Updates a sign and calls a SignChangeEvent.
     * 
     * @param p The player who updated the sign.
     * @param s The sign you want to update.
     */
    public void updateSign(Player p, Sign s) {
        //The first update is used to change the text of the sign just in case the SignChangeEvent blocks it. This is used mostly to support Lockette.
        s.update();

        getServer().getPluginManager().callEvent(new SignChangeEvent(s.getBlock(), p, s.getLines()));
        //We must update again after the SignChangeEvent for colors to work. This also allows other plugins to be compatible.
        s.update();
    }

    public static void say(Player p, String s) {
        p.sendMessage(ChatColor.GOLD + "[Sign Editor] " + ChatColor.WHITE + s);
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
