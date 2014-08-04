package signdoctor.commands;

import java.util.List;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import signdoctor.InvalidLineException;
import signdoctor.SignDoctor;

public abstract class SignCommand implements CommandExecutor {

    protected Plugin plugin;
    protected boolean supportsMulti = true;

    public SignCommand() {
        this.plugin = SignDoctor.plugin;
    }

    protected void say(Player p, String s) {
        SignDoctor.say(p, s);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage("Sign Doctor: You must be a player for this to work.");
            return true;
        }

        if (!player.hasPermission(SignDoctor.PERM_EDIT)) {
            say(player, "You do not have permission to edit signs.");
            return true;
        }

        if (!SignDoctor.isEditing(player)) {
            say(player, SignDoctor.MSG_EDIT_DISABLED);
            return true;
        }

        Sign sign = (Sign) SignDoctor.getMetadata(player, SignDoctor.SIGN, plugin);
        if (!SignDoctor.isMultiEditing(player) && sign == null) {
            say(player, SignDoctor.MSG_NO_ACTIVE_SIGN);
            return true;
        }

        List<Sign> signs = SignDoctor.getActiveSigns(player);
        if (supportsMulti && SignDoctor.isMultiEditing(player)) {
            boolean noSignSelected = false;
            if (signs != null) {
                if (signs.size() < 1) {
                    noSignSelected = true;
                }
            } else {
                noSignSelected = true;
            }
            if (noSignSelected) {
                say(player, "No signs selected.");
                return true;
            }
        }

        try {
            if (supportsMulti && SignDoctor.isMultiEditing(player)) {
                boolean result = true;
                for (Sign s : signs) {
                    if (onCommand(player, command, label, args, s) == false) {
                        result = false;
                    }
                }
                return result;
            } else {
                return onCommand(player, command, label, args, sign);
            }
        } catch (InvalidLineException e) {
            say(player, e.getMessage());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected abstract boolean onCommand(Player player, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException;

}
