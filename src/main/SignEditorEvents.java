package main;

import org.bukkit.block.*;
import org.bukkit.event.*;
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
		e.getPlayer().setMetadata(SignEditor.signEdit, new FixedMetadataValue(SignEditor.plugin, false));
		e.getPlayer().setMetadata(SignEditor.signLines, new FixedMetadataValue(SignEditor.plugin, new String[4]));
	}

	@EventHandler()
	public void onInteract(PlayerInteractEvent e) {
		try {
			if ((boolean) SignEditor.getMetadata(e.getPlayer(), SignEditor.signEdit, SignEditor.plugin)) {
				if (e.getClickedBlock().getState() instanceof Sign) {
					Sign s = (Sign) e.getClickedBlock().getState();
					e.getPlayer().setMetadata(SignEditor.sign, new FixedMetadataValue(SignEditor.plugin, s));
					e.getPlayer().sendMessage("Sign active.");
				}
			}
		} catch (NullPointerException exception) {
			e.getPlayer().sendMessage("Sign could not be activated, please try again.");
		}
	}
}
