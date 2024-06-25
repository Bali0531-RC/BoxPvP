package org.bali.boxpvp.config.impl;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bali.boxpvp.Main;
import org.bali.boxpvp.config.AbstractConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Config implements AbstractConfig {
    private YamlDocument file = null;

    @Override
    public void setup() {
        try {
            file = YamlDocument.create(new File(Main.getInstance().getDataFolder(), "config.yml"), Main.getInstance().getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("version")).build());
                    file.update();
            if (!file.contains("arenas")) {
                // If it doesn't exist, add it to the config.yml file
                file.set("arenas", new ArrayList<>());
                file.update();
                saveConfig(); // Save the changes to the config.yml file
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public YamlDocument getConfig() {
        return file;
    }

    @Override
    public void reloadConfig() {
        try {
            file.reload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void saveConfig() {
        try {
            file.save();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
