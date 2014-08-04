package signdoctor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;

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
