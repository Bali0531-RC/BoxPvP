package org.bali.boxpvp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Settings {

    private final static Settings instance = new Settings();
    private File file;
    private FileConfiguration config;
    private boolean boxPvpEnabled;

    private Settings() {
    }

    public void load() {
        file = new File(Main.getInstance().getDataFolder(), "settings.yml");

        if (!file.exists()) {
            Main.getInstance().saveResource("settings.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
        boxPvpEnabled = config.getBoolean("boxpvp", false);
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public boolean isBoxPvpEnabled() {
        return boxPvpEnabled;
    }

    public void setBoxPvpEnabled(boolean boxPvpEnabled) {
        this.boxPvpEnabled = boxPvpEnabled;
        set("boxpvp", boxPvpEnabled);
    }

    public static Settings getInstance() {
        return instance;
    }
}
