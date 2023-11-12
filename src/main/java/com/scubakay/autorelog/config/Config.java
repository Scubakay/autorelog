package com.scubakay.autorelog.config;

import com.scubakay.autorelog.AutoRelogClient;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;

public class Config {
    public int timeBeforeReconnecting;

    final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .path(Path.of("config/autorelog.conf"))
            .build();

    CommentedConfigurationNode root;

    public Config() {
        load();
    }

    public void load() {
        try {
            root = loader.load();
            if (root.empty()) {
                AutoRelogClient.LOGGER.info("No AutoRelog config found, loading default");
                timeBeforeReconnecting = 30;
                save();
            } else {
                timeBeforeReconnecting = root.node("timeBeforeReconnecting").getInt();
                AutoRelogClient.LOGGER.info("Loaded AutoRelog config");
            }
        } catch (IOException e) {
            AutoRelogClient.LOGGER.info("An error occurred while loading AutoRelog configuration: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }
    }

    private void save() {
        try {
            root.node("timeBeforeReconnecting").set(Integer.class, timeBeforeReconnecting);
            loader.save(root);
        } catch (final ConfigurateException e) {
            AutoRelogClient.LOGGER.info("Unable to save your AutoRelog configuration! Sorry! " + e.getMessage());
        }
    }
}
