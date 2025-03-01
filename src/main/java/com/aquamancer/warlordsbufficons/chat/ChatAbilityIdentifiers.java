package com.aquamancer.warlordsbufficons.chat;

import com.aquamancer.warlordsbufficons.FileManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides methods that identify whether a String(chat message) indicates an event that affects buffs/debuffs.
 */
public class ChatAbilityIdentifiers {
    /*
     * We split the identifiers into those with the bit left and right shift operators, to reduce the amount
     * of contains() checks per chat message.
     */
    /**
     * List of Function with parameter incoming chat string, and returns the universal name, or null if no match.
     */
    private static List<Function<String, String>> operationsSelf;
    /**
     * Universal name mapped with a supplier of boolean (whether the chat contains that identifier) for
     * chat messages with the bit left shift operator.
     */
    private static List<Function<String, String>> operationsExternal;

    private static final String JSON_NAME = "chat-identifiers.json";
    private static final Logger LOGGER = LogManager.getLogger(ChatAbilityIdentifiers.class);
    public static void init() {
        operationsSelf = new ArrayList<>();
        operationsExternal = new ArrayList<>();

        JsonObject chatIdentifiers = FileManager.getChatIdentifiers();
        if (chatIdentifiers == null) return;
        
        JsonObject incoming = chatIdentifiers.get("self").getAsJsonObject();
        JsonObject outgoing = chatIdentifiers.get("external").getAsJsonObject();
        for (Map.Entry<String, JsonElement> status : incoming.entrySet()) {
            operationsSelf.add(s -> s.contains(status.getValue().getAsJsonObject().get("contains").getAsString()) ? status.getKey() : null);
        }
        for (Map.Entry<String, JsonElement> status : outgoing.entrySet()) {
            operationsExternal.add(s -> s.contains(status.getValue().getAsJsonObject().get("contains").getAsString()) ? status.getKey() : null);
        }
    }
    public static String getMatchSelf(String chatMessage) {
        for (int i = 0; i < operationsSelf.size(); i++) {
            String result = operationsSelf.get(i).apply(chatMessage);
            if (result != null)
                return result;
        }
        return null;
    }
    public static String getMatchExternal(String chatMessage) {
        for (int i = 0; i < operationsExternal.size(); i++) {
            String result = operationsExternal.get(i).apply(chatMessage);
            if (result != null)
                return result;
        }
        return null;
    }
}
   