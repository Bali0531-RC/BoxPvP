package org.bali.boxpvp.config.impl;

import org.bali.boxpvp.Main;
import org.bali.boxpvp.config.AbstractConfig;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.io.IOException;
import static org.bali.boxpvp.Main.CONFIG;

public class Messages implements AbstractConfig {
    private YamlDocument file = null;
    private final File langDir = new File(Main.getInstance().getDataFolder(), "lang");

    @Override
    public void setup() {
        try {
            if (!langDir.exists()) {
                langDir.mkdirs();
            }
            String languageFile = "messages" + CONFIG.getString("language") + ".yml";
            File messageFile = new File(langDir, languageFile);
            if (!messageFile.exists()) {
                Main.getInstance().saveResource("lang/" + languageFile, false);
            }
            file = YamlDocument.create(messageFile, Main.getInstance().getResource("lang/" + languageFile), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.DEFAULT, DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("version")).build());
            file.update();
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
}