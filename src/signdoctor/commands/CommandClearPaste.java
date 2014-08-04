package signdoctor.commands;

import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import signdoctor.InvalidLineException;
import signdoctor.SignDoctor;

public class CommandClearPaste extends SignCommand {

    protected boolean paste;
    protected boolean allLines;

    public CommandClearPaste(boolean paste, boolean allLines) {
        this.paste = paste;
        this.allLines = allLines;
    }

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException {
        if (args.length < 1)
            return false;

        if (allLines) {
            if (paste) {
                SignDoctor.pasteSign(sign, (String[]) SignDoctor.getMetadata(p, SignDoctor.SIGN_LINES, plugin));
                SignDoctor.updateSign(p, sign);
            } else {
                SignDoctor.clearSign(sign);
                SignDoctor.updateSign(p, sign);
            }
        } else {
            int line = Integer.parseInt(args[0]) - 1;
            if (paste) {
                SignDoctor.pasteLine(sign, (String) SignDoctor.getMetadata(p, SignDoctor.SIGN_LINE, plugin), line);
                SignDoctor.updateSign(p, sign);
            } else {
                SignDoctor.clearLine(sign, line);
                SignDoctor.updateSign(p, sign);
            }
        }

        return true;
    }

}
