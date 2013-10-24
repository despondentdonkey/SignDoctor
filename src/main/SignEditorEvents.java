package main;

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
        e.getPlayer().setMetadata(SignEditor.SIGN_EDIT, new FixedMetadataValue(SignEditor.plugin, SignEditor.enableEditing));
        e.getPlayer().setMetadata(SignEditor.SIGN_LINES, new FixedMetadataValue(SignEditor.plugin, new String[4]));
    }

    @EventHandler()
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            Block clickedBlock = e.getClickedBlock();
            BlockState clickedBlockState = clickedBlock.getState();

            if ((boolean) SignEditor.getMetadata(p, SignEditor.SIGN_EDIT, SignEditor.plugin)) {
                if (clickedBlockState instanceof Sign) {
                    Sign s = (Sign) clickedBlockState;

                    p.setMetadata(SignEditor.SIGN, new FixedMetadataValue(SignEditor.plugin, s));
                    SignEditor.say(p, "Sign active.");
                }
            }
        }
    }
}
