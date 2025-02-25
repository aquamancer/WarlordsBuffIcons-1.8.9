package com.aquamancer.warlordsbufficons;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class FileManager {
    private static final Logger LOGGER = LogManager.getLogger(FileManager.class);
    private static final ClassLoader classLoader = FileManager.class.getClassLoader();

    private static File ROOT = new File(Minecraft.getMinecraft().mcDataDir, "warlordsbufficons-1.8.9");

    private static JsonObject defaultChatIdentifiers;
    private static JsonObject defaultStatuses;
    private static JsonObject chatIdentifiers;
    private static JsonObject statuses;

    public static void init() {
        if (!ROOT.exists() || !ROOT.isDirectory()) {
            if (ROOT.mkdir()) {
                LOGGER.debug("successfully created root mod directory");
                JsonParser parser = new JsonParser();
                defaultChatIdentifiers = parser.parse(new InputStreamReader(classLoader.getResourceAsStream("chat-identifiers.json"))).getAsJsonObject();
            } else {
                LOGGER.error("could not create root mod directory");
            }
        }
    }
}
