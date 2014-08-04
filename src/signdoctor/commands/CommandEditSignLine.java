package signdoctor.commands;

import java.util.*;
import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import signdoctor.*;

public class CommandEditSignLine extends SignCommand {

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException {
        if (args.length < 1)
            return false;

        int line = Integer.parseInt(args[0]) - 1;
        SignDoctor.editSignln(sign, line, Arrays.copyOfRange(args, 1, args.length));
        SignDoctor.updateSign(p, sign);
        return true;
    }

}
