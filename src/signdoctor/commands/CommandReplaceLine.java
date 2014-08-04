package signdoctor.commands;

import java.util.regex.*;
import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import signdoctor.*;

public class CommandReplaceLine extends SignCommand {

    protected boolean replaceAll;

    public CommandReplaceLine(boolean replaceAll) {
        this.replaceAll = replaceAll;
    }

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException {
        String replacement = "";
        int line = Integer.parseInt(args[0]) - 1;

        if (args.length < 2)
            return false;
        else if (args.length >= 3)
            replacement = args[2];

        try {
            SignDoctor.replaceln(sign, line, args[1], replacement, replaceAll);
        } catch (PatternSyntaxException e) {
            say(p, "Regex syntax is incorrect.");
            return true;
        }

        SignDoctor.updateSign(p, sign);
        return true;
    }

}
