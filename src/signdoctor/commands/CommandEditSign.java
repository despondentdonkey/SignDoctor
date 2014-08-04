package signdoctor.commands;

import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import signdoctor.*;

public class CommandEditSign extends SignCommand {

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) {
        SignDoctor.editSign(sign, args);
        SignDoctor.updateSign(p, sign);
        return true;
    }

}
