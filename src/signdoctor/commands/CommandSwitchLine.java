package signdoctor.commands;

import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import signdoctor.InvalidLineException;
import signdoctor.SignDoctor;

public class CommandSwitchLine extends SignCommand {

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException {
        if (args.length < 2) {
            return false;
        }

        int lineTargetNum = Integer.parseInt(args[0]) - 1;
        int lineDestNum = Integer.parseInt(args[1]) - 1;
        SignDoctor.switchln(sign, lineTargetNum, lineDestNum);
        SignDoctor.updateSign(p, sign);
        return true;
    }

}
