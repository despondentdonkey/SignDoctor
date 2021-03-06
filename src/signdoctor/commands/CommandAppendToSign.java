package signdoctor.commands;

import java.util.Arrays;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import signdoctor.InvalidLineException;
import signdoctor.SignDoctor;

public class CommandAppendToSign extends SignCommand {

    protected boolean prepend = false;

    public CommandAppendToSign(boolean prepend) {
        this.prepend = prepend;
    }

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException {
        if (args.length < 2)
            return false;

        int line = Integer.parseInt(args[0]) - 1;
        if (prepend) {
            SignDoctor.prependToSign(sign, line, Arrays.copyOfRange(args, 1, args.length));
        } else {
            SignDoctor.appendToSign(sign, line, Arrays.copyOfRange(args, 1, args.length));
        }
        SignDoctor.updateSign(p, sign);
        return true;
    }

}
