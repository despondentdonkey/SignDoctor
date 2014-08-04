package signdoctor.commands;

import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import signdoctor.*;

public class CommandClearCopyCutPasteLine extends SignCommand {

    public static enum Task {
        CLEAR, COPY, CUT, PASTE
    }

    protected Task task;

    public CommandClearCopyCutPasteLine(Task task) {
        this.task = task;
    }

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException {
        if (args.length < 1)
            return false;

        int line = Integer.parseInt(args[0]) - 1;

        if (task == Task.CLEAR) {
            SignDoctor.clearLine(sign, line);
            SignDoctor.updateSign(p, sign);
        } else if (task == Task.COPY) {
            SignDoctor.copyLine(p, sign, line);
            say(p, "Line " + (line + 1) + " has been copied.");
        } else if (task == Task.CUT) {
            SignDoctor.copyLine(p, sign, line);
            SignDoctor.clearLine(sign, line);
            SignDoctor.updateSign(p, sign);
        } else if (task == Task.PASTE) {
            SignDoctor.pasteLine(sign, (String) SignDoctor.getMetadata(p, SignDoctor.SIGN_LINE, plugin), line);
            SignDoctor.updateSign(p, sign);
        }

        return true;
    }

}
