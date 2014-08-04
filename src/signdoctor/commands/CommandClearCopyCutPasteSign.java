package signdoctor.commands;

import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import signdoctor.InvalidLineException;
import signdoctor.SignDoctor;

public class CommandClearCopyCutPasteSign extends SignCommand {

    public static enum Task {
        CLEAR, COPY, CUT, PASTE
    }

    protected Task task;

    public CommandClearCopyCutPasteSign(Task task) {
        this.task = task;
    }

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException {
        if (task == Task.CLEAR) {
            SignDoctor.clearSign(sign);
            SignDoctor.updateSign(p, sign);
        } else if (task == Task.COPY) {
            SignDoctor.copySign(p, sign);
            say(p, "Sign has been copied.");
        } else if (task == Task.CUT) {
            SignDoctor.copySign(p, sign);
            SignDoctor.clearSign(sign);
            SignDoctor.updateSign(p, sign);

            say(p, "Sign has been cut.");
        } else if (task == Task.PASTE) {
            SignDoctor.pasteSign(sign, (String[]) SignDoctor.getMetadata(p, SignDoctor.SIGN_LINES, plugin));
            SignDoctor.updateSign(p, sign);
        }

        return true;
    }

}
