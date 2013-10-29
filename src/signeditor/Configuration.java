package signeditor;

import java.io.*;
import java.util.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.*;

public class Configuration {
    public boolean enableEditing = false;
    public String spacingStr = "_";
    public String blankStr = "\\n";
    public String selectorItem = "FEATHER";

    private final Plugin plugin;
    private final FileConfiguration config;

    public Configuration(Plugin p) {
        this.plugin = p;
        config = plugin.getConfig();

        //Save but do not replace if it exists.
        plugin.saveResource("config.yml", false);

        for (String key : config.getKeys(false)) {
            plugin.getLogger().info(key);
        }

        //Load config from the folder.
        FileConfiguration jarConfig = new YamlConfiguration();
        try {
            jarConfig.load(plugin.getResource("config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        //Removes lines from the folder config if the jar config no longer contains them.
        Set<String> jarKeys = jarConfig.getKeys(false);
        Boolean changed = false;

        for (String configKey : config.getKeys(false)) {
            if (!jarKeys.contains(configKey)) {
                config.set(configKey, null);
                changed = true;
            }
        }

        if (changed) {
            plugin.saveConfig();
        }

        //If the folder config is missing a key from the jar then overwrite the config with the jar config.
        Set<String> keys = config.getKeys(false);
        Boolean rewrite = false;

        for (String jarKey : jarKeys) {
            if (!keys.contains(jarKey)) {
                rewrite = true;
            }
        }

        if (rewrite) {
            plugin.saveResource("config.yml", true);
        }

        assignKeys();
    }

    public void assignKeys() {
        enableEditing = config.getBoolean("enableEditingByDefault");
        spacingStr = config.getString("spacingStr");
        blankStr = config.getString("blankStr");
        selectorItem = config.getString("selectorItem").toUpperCase();
    }
}
