package main;

import org.bukkit.block.*;
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
        e.getPlayer().setMetadata(SignEditor.signEdit, new FixedMetadataValue(SignEditor.plugin, SignEditor.enableEditing));
        e.getPlayer().setMetadata(SignEditor.signLines, new FixedMetadataValue(SignEditor.plugin, new String[4]));
    }

    @EventHandler()
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if ((boolean) SignEditor.getMetadata(e.getPlayer(), SignEditor.signEdit, SignEditor.plugin)) {
                Block clickedBlock = e.getClickedBlock();

                BlockState clickedBlockState = clickedBlock.getState();

                if (clickedBlockState instanceof Sign) {
                    Sign s = (Sign) clickedBlockState;
                    Sign prevSign = (Sign) SignEditor.getMetadata(e.getPlayer(), SignEditor.sign, SignEditor.plugin);

                    if (prevSign != null && prevSign.equals(s)) {
                        e.getPlayer().setMetadata(SignEditor.sign, new FixedMetadataValue(SignEditor.plugin, null));
                        SignEditor.say(e.getPlayer(), "Sign inactive.");
                    } else {
                        e.getPlayer().setMetadata(SignEditor.sign, new FixedMetadataValue(SignEditor.plugin, s));
                        SignEditor.say(e.getPlayer(), "Sign active.");
                    }
                }
            }
        }
    }
}
