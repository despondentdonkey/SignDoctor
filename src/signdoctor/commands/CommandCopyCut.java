package signdoctor.commands;

import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import signdoctor.InvalidLineException;
import signdoctor.SignDoctor;

public class CommandCopyCut extends SignCommand {

    protected boolean cut;
    protected boolean allLines;

    public CommandCopyCut(boolean cut, boolean allLines) {
        this.cut = cut;
        this.allLines = allLines;
        this.supportsMulti = false;
    }

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException {
        if (args.length < 1)
            return false;

        if (allLines) {
            if (cut) {
                SignDoctor.copySign(p, sign);
                SignDoctor.clearSign(sign);
                SignDoctor.updateSign(p, sign);

                say(p, "Sign has been cut.");
            } else {
                SignDoctor.copySign(p, sign);
                say(p, "Sign has been copied.");
            }
        } else {
            int line = Integer.parseInt(args[0]) - 1;
            if (cut) {
                SignDoctor.copyLine(p, sign, line);
                SignDoctor.clearLine(sign, line);
                SignDoctor.updateSign(p, sign);
            } else {
                SignDoctor.copyLine(p, sign, line);
                say(p, "Line " + (line + 1) + " has been copied.");
            }
        }

        return true;
    }

}
