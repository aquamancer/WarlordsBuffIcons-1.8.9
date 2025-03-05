package com.aquamancer.warlordsbufficons;

import com.google.gson.*;
import com.google.gson.stream.MalformedJsonException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final Logger LOGGER = LogManager.getLogger(FileManager.class);
    private static final ClassLoader classLoader = FileManager.class.getClassLoader();

    private static final File MC_CONFIG_DIR = new File(Minecraft.getMinecraft().mcDataDir, "config");
    private static final File ROOT_DIR = new File(MC_CONFIG_DIR,"warlordsbufficons-1.8.9");
    private static final File CONFIG_DIR = new File(ROOT_DIR, "config");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "config.json");
    private static final File STATUSES_DIR = new File(CONFIG_DIR, "statuses");
    private static final File ASSETS_DIR = new File(ROOT_DIR, "assets");
    private static final File RESOURCE_DIR = new File(ASSETS_DIR, "warlordsbufficons-1.8.9");

    /**
     * Files to copy from resources to mod folder on the first program execution.
     */
    // todo copy directory instead
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
        loadResources();
        
        if (firstLaunch) {
            for (String preset : STATUSES_PRESETS) {
                try {
                    Files.copy(classLoader.getResourceAsStream(preset), new File(STATUSES_DIR, preset).toPath());
                } catch (IOException ex) {
                    LOGGER.warn("failure copying statuses preset: {} to .minecraft file system. does it already exist?", preset);
                }
            }
        }
    }

    public static void loadActiveConfigs(JsonParser parser) {
        loadConfigFile(parser);
        loadChatIdentifiersFile(parser);
        loadStatusesFile(parser);
    }
    private static void loadConfigFile(JsonParser parser) {
        try (BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE))){
            config = parser.parse(configReader).getAsJsonObject();
        } catch (Exception ex) {
            LOGGER.warn("failed reading {}. copying config defaults", CONFIG_FILE);
            try {
                Files.copy(classLoader.getResourceAsStream("config.json"), CONFIG_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING);
                try (BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE))) {
                    config = parser.parse(configReader).getAsJsonObject();
                } catch (IOException | RuntimeException ex1) {
                    LOGGER.error("error while reading default config file.");
                }
            } catch (IOException ex1) {
                LOGGER.error("failed copying and overwriting the default config file.");
            }
        }
    }
    private static void loadChatIdentifiersFile(JsonParser parser) {
        try (BufferedReader chatIdentifiersReader = new BufferedReader(new FileReader(new File(CONFIG_DIR, config.get("chatIdentifiers").getAsString())))) {
            chatIdentifiers = parser.parse(chatIdentifiersReader).getAsJsonObject();
        } catch (Exception ex) {
            LOGGER.warn("error parsing chat identifiers file: {}. resetting to default", config.get("chatIdentifiers").getAsString());
            File defaultChatIdentifiers = new File(CONFIG_DIR, "chat-identifiers.json");
            try {
                Files.copy(classLoader.getResourceAsStream("chat-identifiers.json"), defaultChatIdentifiers.toPath());
            } catch (FileAlreadyExistsException existsException) {
            } catch (IOException ioException) {
                LOGGER.error("chat identifiers json file specified by config file does not exist, and there was an error copying the default from resources to the .minecraft file system");
            }
            try (BufferedWriter configWriter = new BufferedWriter(new FileWriter(CONFIG_FILE));
                 BufferedReader defaultReader = new BufferedReader(new FileReader(defaultChatIdentifiers));
            ) {
                chatIdentifiers = parser.parse(defaultReader).getAsJsonObject();
                config.addProperty("chatIdentifiers", "chat-identifiers.json");
                Gson gson = new Gson();
                gson.toJson(config, configWriter);
            } catch (MalformedJsonException | JsonParseException jsonParseException) {
                LOGGER.error("chat-identifiers.json is malformed");
            } catch (Exception ex1) {
                LOGGER.error("error modifying config file while resetting chatIdentifiers to default because it didn't exist");
            }
        }
    }
    private static void loadStatusesFile(JsonParser parser) {
        try (BufferedReader statusesReader = new BufferedReader(new FileReader(new File(STATUSES_DIR, config.get("statuses").getAsString())))) {
            statuses = parser.parse(statusesReader).getAsJsonObject();
        } catch (Exception ex) {
            LOGGER.warn("could not locate statuses file: {}. resetting to default", config.get("statuses").getAsString());
            File defaultStatuses = new File(STATUSES_DIR, "statuses.json");
            try {
                Files.copy(classLoader.getResourceAsStream("statuses.json"), defaultStatuses.toPath());
            } catch (FileAlreadyExistsException ex1) {
            } catch (IOException ioException) {
                LOGGER.error("statuses json file specified by config file does not exist, and there was an error copying the default from resources to the .minecraft file system");
            }
            try (BufferedWriter configWriter = new BufferedWriter(new FileWriter(CONFIG_FILE));
                 BufferedReader reader = new BufferedReader(new FileReader(defaultStatuses));
            ) {
                statuses = parser.parse(reader).getAsJsonObject();
                config.addProperty("statuses", "statuses.json");
                Gson gson = new Gson();
                gson.toJson(config, configWriter);
            } catch (MalformedJsonException | JsonParseException jsonParseException) {
                LOGGER.error("statuses.json is malformed");
            } catch (Exception ex1) {
                LOGGER.error("error modifying config file while resetting statuses to default because it didn't exist");
            }
        }
    }
    public static void loadResources() {
        if (!RESOURCE_DIR.exists() || !RESOURCE_DIR.isDirectory()) {
            try {
                FileUtils.copyDirectory(new File(classLoader.getResource("assets/warlordsbufficons-1.8.9").toURI()), RESOURCE_DIR);
            } catch (IOException | URISyntaxException | NullPointerException ex) {
                LOGGER.error("could not copy resource pack from resources to .minecraft filesystem.");
                return;
            }
        }
        FolderResourcePack resourcePack = new FolderResourcePack(RESOURCE_DIR);
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        if (resourceManager instanceof SimpleReloadableResourceManager) {
            ((SimpleReloadableResourceManager) resourceManager).reloadResourcePack(resourcePack);
        } else {
            LOGGER.error("resourceManager is not an instance of SimpleReloadableResourceManager. Couldn't load resource pack.");
        }
    }
    @Nullable
    public static JsonObject getStatuses() {
        return statuses;
    }
    @Nullable
    public static JsonObject getChatIdentifiers() {
        return chatIdentifiers;
    }
    @Nullable
    public static JsonObject getConfig() {
        return config;
    }
    public static File getAssetsDir() {
        return ASSETS_DIR;
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
