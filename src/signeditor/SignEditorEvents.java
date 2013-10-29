package signeditor;

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
public class SignEditorEvents implements Listener {

    @EventHandler()
    public void onLogin(PlayerLoginEvent e) {
        boolean enableEditing = e.getPlayer().hasPermission(SignEditor.PERM_EDIT) ? SignEditor.config.enableEditing : false;

        e.getPlayer().setMetadata(SignEditor.SIGN_EDIT, new FixedMetadataValue(SignEditor.plugin, enableEditing));
        e.getPlayer().setMetadata(SignEditor.SIGN_LINES, new FixedMetadataValue(SignEditor.plugin, new String[4]));
    }

    @EventHandler()
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();

            if (SignEditor.noSelector || p.getItemInHand().getData().getItemType() == Material.getMaterial(SignEditor.config.selectorItem)) {
                Block clickedBlock = e.getClickedBlock();
                BlockState clickedBlockState = clickedBlock.getState();

                if (SignEditor.isEditing(p)) {
                    if (clickedBlockState instanceof Sign) {
                        Sign s = (Sign) clickedBlockState;

                        p.setMetadata(SignEditor.SIGN, new FixedMetadataValue(SignEditor.plugin, s));
                        SignEditor.say(p, "Sign active.");
                    }
                }
            }
        }
    }
}
