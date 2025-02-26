package com.aquamancer.warlordsbufficons;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileManager {
    private static final Logger LOGGER = LogManager.getLogger(FileManager.class);
    private static final ClassLoader classLoader = FileManager.class.getClassLoader();

    private static final File MC_CONFIG_DIR = new File(Minecraft.getMinecraft().mcDataDir, "config");
    private static final File ROOT_DIR = new File(MC_CONFIG_DIR,"warlordsbufficons-1.8.9");
    private static final File CONFIG_DIR = new File(ROOT_DIR, "config");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "config.json");
    private static final File STATUSES_DIR = new File(CONFIG_DIR, "statuses");
    private static final File ASSETS_DIR = new File(ROOT_DIR, "assets");

    /**
     * Files to copy from resources to mod folder on the first program execution.
     */
    private static final String[] STATUSES_PRESETS = new String[] {};

    private static JsonObject defaultChatIdentifiers;
    private static JsonObject defaultStatuses;
    private static JsonObject defaultConfig;
    
    private static JsonObject chatIdentifiers;
    private static JsonObject statuses;
    private static JsonObject config;

    public static void init() {
        JsonParser parser = new JsonParser();
        defaultChatIdentifiers = parser.parse(new InputStreamReader(classLoader.getResourceAsStream("chat-identifiers.json"))).getAsJsonObject();
        defaultStatuses = parser.parse(new InputStreamReader(classLoader.getResourceAsStream("statuses.json"))).getAsJsonObject();
        defaultConfig = parser.parse(new InputStreamReader(classLoader.getResourceAsStream("config.json"))).getAsJsonObject();
        
        boolean firstLaunch = !ROOT_DIR.exists() || !ROOT_DIR.isDirectory();
        
        try {
            Files.createDirectories(STATUSES_DIR.toPath());
            Files.createDirectories(ASSETS_DIR.toPath());
        } catch (IOException ex) {
            LOGGER.error("failure creating mod directories");
        }
        
        loadActiveConfigs(parser);
        
        if (firstLaunch) {
            for (String preset : STATUSES_PRESETS) {
                try {
                    Files.copy(classLoader.getResourceAsStream(preset), new File(STATUSES_DIR, preset).toPath());
                } catch (IOException ex) {
                    LOGGER.warn("failure copying statuses preset: {} to .minecraft file system", preset);
                }
            }
        }
    }

    private static void loadActiveConfigs(JsonParser parser) {
        try (BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE))){
            config = parser.parse(configReader).getAsJsonObject();
        } catch (IOException | RuntimeException ex) {
            LOGGER.warn("failed reading {}. copying config defaults", CONFIG_FILE);
            try {
                Files.copy(classLoader.getResourceAsStream("config.json"), CONFIG_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING);
                try (BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE))) {
                    config = parser.parse(configReader).getAsJsonObject();
                } catch (IOException ex1) {
                    LOGGER.error("error while reading default config file.");
                }
            } catch (IOException ex1) {
                LOGGER.error("failed copying and overwriting the default config file.");
            }
        }
        try {
            chatIdentifiers = parser.parse(new FileReader(new File(CONFIG_DIR, config.get("chatIdentifiers").getAsString()))).getAsJsonObject();
        } catch (FileNotFoundException ex) {
            LOGGER.warn("could not locate chat identifiers file: {}. resetting to default", config.get("chatIdentifiers").getAsString());
            File defaultChatIdentifiers = new File(CONFIG_DIR, "chat-identifiers.json");
            if (!defaultChatIdentifiers.exists()) {
                try {
                    Files.copy(classLoader.getResourceAsStream("chat-identifiers.json"), defaultChatIdentifiers.toPath());
                } catch (IOException ioException) {
                    LOGGER.error("chat identifiers json file specified by config file does not exist, and there was an error copying the default from resources to the .minecraft file system");
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
                config.addProperty("chatIdentifiers", "chat-identifiers.json");
                Gson gson = new Gson();
                gson.toJson(config, writer);
            } catch (IOException ioException) {
                LOGGER.error("error modifying config file while resetting chatIdentifiers to default because it didn't exist");
            }
        }
        try {
            statuses = parser.parse(new FileReader(new File(STATUSES_DIR, config.get("statuses").getAsString()))).getAsJsonObject();
        } catch (FileNotFoundException ex) {
            LOGGER.warn("could not locate statuses file: {}. resetting to default", config.get("statuses").getAsString());
            File defaultStatuses = new File(STATUSES_DIR, "statuses.json");
            if (!defaultStatuses.exists()) {
                try {
                    Files.copy(classLoader.getResourceAsStream("statuses.json"), defaultStatuses.toPath());
                } catch (IOException ioException) {
                    LOGGER.error("statuses json file specified by config file does not exist, and there was an error copying the default from resources to the .minecraft file system");
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
                config.addProperty("statuses", "statuses.json");
                Gson gson = new Gson();
                gson.toJson(config, writer);
            } catch (IOException ioException) {
                LOGGER.error("error modifying config file while resetting statuses to default because it didn't exist");
            }
        }
    }
    
    public static JsonObject getStatuses() {
        return statuses;
    }

    public static JsonObject getChatIdentifiers() {
        return chatIdentifiers;
    }
    
    public static JsonObject getConfig() {
        return config;
    }

    public static JsonObject getDefaultConfig() {
        return defaultConfig;
    }

    public static JsonObject getDefaultStatuses() {
        return defaultStatuses;
    }

    public static JsonObject getDefaultChatIdentifiers() {
        return defaultChatIdentifiers;
    }
}
