package signdoctor;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.metadata.*;

/**
 * Event handler class.
 *
 * @author Parker Miller
 *
 */
public class SignDoctorEvents implements Listener {

    @EventHandler()
    public void onLogin(PlayerLoginEvent e) {
        boolean enableEditing = e.getPlayer().hasPermission(SignDoctor.PERM_EDIT) ? SignDoctor.config.enableEditing : false;

        e.getPlayer().setMetadata(SignDoctor.SIGN_EDIT, new FixedMetadataValue(SignDoctor.plugin, enableEditing));
        e.getPlayer().setMetadata(SignDoctor.SIGN_LINES, new FixedMetadataValue(SignDoctor.plugin, new String[4]));
        e.getPlayer().setMetadata(SignDoctor.SIGN_LINE, new FixedMetadataValue(SignDoctor.plugin, ""));
    }

    @EventHandler()
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();

            if (SignDoctor.noSelector || p.getItemInHand().getData().getItemType() == Material.getMaterial(SignDoctor.config.selectorItem)) {
                Block clickedBlock = e.getClickedBlock();
                BlockState clickedBlockState = clickedBlock.getState();

                if (SignDoctor.isEditing(p)) {
                    if (clickedBlockState instanceof Sign) {
                        Sign s = (Sign) clickedBlockState;

                        p.setMetadata(SignDoctor.SIGN, new FixedMetadataValue(SignDoctor.plugin, s));
                        SignDoctor.say(p, "Sign active.");
                    }
                }
            }
        }
    }
}
