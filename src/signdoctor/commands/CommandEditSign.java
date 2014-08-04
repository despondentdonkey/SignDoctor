package signdoctor.commands;

import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import signdoctor.SignDoctor;

public class CommandEditSign extends SignCommand {

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) {
        SignDoctor.editSign(sign, args);
        SignDoctor.updateSign(p, sign);
        return true;
    }

}
