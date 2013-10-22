package main;

import java.util.*;
import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

/**
 * Sign Editor Plugin Class.
 * 
 * @author Parker Miller
 * 
 */
public class SignEditor extends JavaPlugin {
	public static Plugin plugin;

	public static String signEdit = "SignEditor_editSign";
	public static String sign = "SignEditor_activeSign";
	public static String signLines = "SignEditor_signLinesArray";

	@Override
	public void onEnable() {
		plugin = this;
		//Register events.
		getServer().getPluginManager().registerEvents(new SignEditorEvents(), this);
	}

	@Override
	public void onDisable() {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Toggle sign edit command.
		if (cmd.getName().equalsIgnoreCase("toggleSignEdit")) {
			Player p = (Player) sender;
			boolean editing = (boolean) getMetadata(p, signEdit, this);
			if (editing)
				p.setMetadata(signEdit, new FixedMetadataValue(this, false));
			else
				p.setMetadata(signEdit, new FixedMetadataValue(this, true));
			sender.sendMessage(!editing ? "Sign editing has been enabled." : "Sign editing has been disabled.");
			return true;
		}

		//Edit sign command.
		if (cmd.getName().equalsIgnoreCase("editSign")) {
			Player p = (Player) sender;
			boolean editing = (boolean) getMetadata(p, signEdit, this);
			Sign sign = (Sign) getMetadata(p, SignEditor.sign, this);
			if (editing) {
				if (sign != null) {
					for (int i = 0; i < args.length; i++) {
						if (!args[i].equals("\\n")) {
							char[] c = args[i].toCharArray();

							for (char ch : c) {
								if (ch == '_')
									args[i] = args[i].replace(ch, ' ');
							}

							sign.setLine(i, args[i]);
						}
					}
					sign.update();
					return true;
				} else {
					sender.sendMessage("No sign is active.");
					return true;
				}
			} else {
				noSignEditMessage(sender);
				return true;
			}
		}

		//Edit sign line command.
		if (cmd.getName().equalsIgnoreCase("editSignln")) {
			Player p = (Player) sender;
			boolean editing = (boolean) getMetadata(p, signEdit, this);
			Sign sign = (Sign) getMetadata(p, SignEditor.sign, this);
			if (editing) {
				if (sign != null) {
					StringBuilder sb = new StringBuilder();
					for (int i = 1; i < args.length; i++) {
						sb.append(args[i]);
						sb.append(" ");
					}

					sign.setLine(Integer.parseInt(args[0]) - 1, sb.toString());
					sign.update();
					return true;
				} else {
					sender.sendMessage("No sign is active.");
					return true;
				}
			} else {
				noSignEditMessage(sender);
				return true;
			}
		}

		if (cmd.getName().equalsIgnoreCase("copySign")) {
			Player p = (Player) sender;
			boolean editing = (boolean) getMetadata(p, signEdit, this);
			Sign sign = (Sign) getMetadata(p, SignEditor.sign, this);
			if (editing) {
				if (sign != null) {
					String[] sl = new String[4];
					for (int i = 0; i < sl.length; i++) {
						sl[i] = sign.getLine(i);
					}
					p.setMetadata(signLines, new FixedMetadataValue(this, sl));
					sender.sendMessage("Sign has been copied.");
					return true;
				} else {
					sender.sendMessage("No sign is active.");
					return true;
				}
			} else {
				noSignEditMessage(sender);
				return true;
			}
		}

		if (cmd.getName().equalsIgnoreCase("pasteSign")) {
			Player p = (Player) sender;
			boolean editing = (boolean) getMetadata(p, signEdit, this);
			Sign sign = (Sign) getMetadata(p, SignEditor.sign, this);
			if (editing) {
				if (sign != null) {
					String[] sl = (String[]) getMetadata(p, signLines, this);
					for (int i = 0; i < sl.length; i++) {
						sign.setLine(i, sl[i]);
					}
					sign.update();
					return true;
				} else {
					sender.sendMessage("No sign is active.");
					return true;
				}
			} else {
				noSignEditMessage(sender);
				return true;
			}
		}

		return false;
	}

	/**
	 * Common message shown when editing is disabled.
	 * 
	 * @param s The player to send the message to.
	 */
	public static void noSignEditMessage(CommandSender s) {
		s.sendMessage("Sign editing is not enabled. To enable, use the command: toggleSignEdit");
	}

	public static void setMetaData(Player p, String key, Object value, Plugin plugin) {
		p.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	public static Object getMetadata(Player player, String key, Plugin plugin) {
		List<MetadataValue> values = player.getMetadata(key);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin().getDescription().getName().equals(plugin.getDescription().getName())) {
				return value.value();
			}
		}
		return null;
	}
}
