package org.bali.boxpvp.config;

import dev.dejvokep.boostedyaml.YamlDocument;

public interface AbstractConfig {
    void setup();
    YamlDocument getConfig();
    void reloadConfig();
    void saveConfig();
}
