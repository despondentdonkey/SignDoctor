package signdoctor.commands;

import java.util.regex.*;
import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import signdoctor.*;

public class CommandReplaceSign extends SignCommand {

    protected boolean replaceAll;

    public CommandReplaceSign(boolean replaceAll) {
        this.replaceAll = replaceAll;
    }

    @Override
    protected boolean onCommand(Player p, Command command, String label, String[] args, Sign sign) throws InvalidLineException, NumberFormatException {
        String replacement = "";
        if (args.length < 1)
            return false;
        else if (args.length >= 2)
            replacement = args[1];

        try {
            for (int line = 0; line < 4; ++line) {
                SignDoctor.replaceln(sign, line, args[0], replacement, replaceAll);
            }
        } catch (PatternSyntaxException e) {
            say(p, "Regex syntax is incorrect.");
            return true;
        }

        SignDoctor.updateSign(p, sign);
        return true;
    }

}
