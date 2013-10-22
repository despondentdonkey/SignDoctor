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
		if ((boolean) SignEditor.getMetadata(e.getPlayer(), SignEditor.signEdit, SignEditor.plugin)) {
			Block clickedBlock = e.getClickedBlock();

			if (clickedBlock != null) {
				BlockState clickedBlockState = clickedBlock.getState();

				if (clickedBlockState instanceof Sign) {
					Sign s = (Sign) clickedBlockState;
					e.getPlayer().setMetadata(SignEditor.sign, new FixedMetadataValue(SignEditor.plugin, s));
					SignEditor.say(e.getPlayer(), "Sign active.");
				}
			} else {
				SignEditor.say(e.getPlayer(), "Do not use a block to select a sign. Instead use your hand or some other item that cannot be placed.");
			}
		}
	}
}
