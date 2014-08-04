package signdoctor.commands;

import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import signdoctor.*;

public abstract class SignCommand implements CommandExecutor {

    protected Plugin plugin;

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

        if (sign == null) {
            say(player, SignDoctor.MSG_NO_ACTIVE_SIGN);
            return true;
        }

        try {
            return onCommand(player, command, label, args, sign);
        } catch (InvalidLineException e) {
            say(player, e.getMessage());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected abstract boolean onCommand(Player player, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException;

}
