package com.aquamancer.warlordsbufficons.handlers;

import com.aquamancer.warlordsbufficons.statuses.BuffEnum;
import com.aquamancer.warlordsbufficons.statuses.DebuffEnum;
import com.aquamancer.warlordsbufficons.statuses.Status;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides methods that identify whether a String(chat message) indicates an event that affects buffs/debuffs.
 */
public class ChatAbilityIdentifiers {
    /**
     * We split the identifiers into those with the bit left and right shift operators, to reduce the amount
     * of contains() checks per chat message.
     */
    /**
     * List of Function with parameter incoming chat string, and returns the universal name, or null if no match.
     */
    private static List<Function<String, String>> operationsIncoming;
    /**
     * Universal name mapped with a supplier of boolean (whether the chat contains that identifier) for
     * chat messages with the bit left shift operator.
     */
    private static List<Function<String, String>> operationsOutgoing;

    private static final String JSON_NAME = "chat-identifiers.json";
    private static final Logger LOGGER = LogManager.getLogger(ChatAbilityIdentifiers.class);
    public static void loadChatMatches() {
        operationsIncoming = new ArrayList<>();
        operationsOutgoing = new ArrayList<>();

        try (InputStream jsonStream = ChatAbilityIdentifiers.class.getClassLoader().getResourceAsStream(JSON_NAME)) {
            if (jsonStream == null) {
                LOGGER.error("parsing chat ability identifier: {} resulted in a null InputStream: ", JSON_NAME);
                // todo temporarily disable chat dependency
                return;
            }
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(new InputStreamReader(jsonStream)).getAsJsonObject();
            JsonObject incoming = json.get("incoming").getAsJsonObject();
            JsonObject outgoing = json.get("outgoing").getAsJsonObject();
            for (Map.Entry<String, JsonElement> status : incoming.entrySet()) {
                operationsIncoming.add(s -> s.contains(status.getValue().getAsJsonObject().get("contains").getAsString()) ? status.getKey() : null);
            }
            for (Map.Entry<String, JsonElement> status : outgoing.entrySet()) {
                operationsOutgoing.add(s -> s.contains(status.getValue().getAsJsonObject().get("contains").getAsString()) ? status.getKey() : null);
            }
        } catch (IOException ex) {
            LOGGER.error("could not load chat ability identifier json file: {}", JSON_NAME);
            // todo temporarily disable chat dependency
        }
    }
    public static String getDebuffMatches(String s) {
        // gather all methods into a List
        List<Supplier<String>> operations = new ArrayList<>();
        operations.add(() -> slowedFrostbolt(s));
        operations.add(() -> slowedFreezingBreath(s));
        operations.add(() -> wounded(s));
        operations.add(() -> crippled(s));
        operations.add(() -> undyingArmyPopped(s));
        
        // collect all debuff matches
        List<String> result = new ArrayList<>();
        for (Supplier<String> operation : operations) {
            // add the Debuff match to result if the method call does not return NONE
            String value = operation.get(); // evaluate the method
            if (value != null)
                result.add(value);
        }
        return result;
    }
    public static List<BuffEnum> getBuffMatches(String s) {
        // gather all methods into a List
        List<Supplier<BuffEnum>> operations = new ArrayList<>();
        operations.add(() -> interveneIncoming(s));
        operations.add(() -> interveneOutgoing(s));
        operations.add(() -> lastStandIncoming(s));
        operations.add(() -> undyingArmy(s));
        
        // collect all buff matches
        List<BuffEnum> result = new ArrayList<>();
        for (Supplier<BuffEnum> operation : operations) {
            // add the Buff match to result if the method call does not return NONE
            BuffEnum value = operation.get(); // evaluate the method
            if (value != BuffEnum.NONE)
                result.add(value);
        }
        return result;
    }
     /*
        received/incoming effects (chat messages with the bit left shift character)
      */
    // pyromancer
    // todo pyro arcane shield stun?
    // cryomancer
    // todo meleeing an ice barrier
    private static String slowedFrostbolt(String s) {
        return s.contains("Frostbolt hit you") ? "slowFrostbolt" : null;
    }
    private static String slowedFreezingBreath(String s) {
        return s.contains("Freezing Breath hit you") ? "slowFreezingBreath" : null;
    }
    // aquamancer
    
    // WARRIOR
    // bers
    public static String wounded(String s) {
        return s.contains("Wounding Strike hit you") ? "wounding" : null;
    }
    // rev
    public static DebuffEnum crippled(String s) {
        return s.contains("Crippling Strike hit you") ? DebuffEnum.CRIPPLED : DebuffEnum.NONE;
    }
    public static DebuffEnum undyingArmyPopped(String s) {
        return s.contains("Undying Army revived you with temporary health") ? DebuffEnum.UNDYING_ARMY_POPPED : DebuffEnum.NONE;
    }
    
    /*
        outgoing effects (chat messages with the bit right shift character)
     */
    public static BuffEnum interveneIncoming(String s) {
        return s.contains("is shielding you with their Intervene") ? BuffEnum.INTERVENE_INCOMING : BuffEnum.NONE;
    }
    public static BuffEnum interveneOutgoing(String s) {
        return s.contains("with your Intervene") ? BuffEnum.INTERVENE_OUTGOING : BuffEnum.NONE;
    }
    public static BuffEnum lastStandIncoming(String s) {
        return s.contains("Last Stand is protecting you") ? BuffEnum.LAST_STAND_INCOMING : BuffEnum.NONE;
    }
    // todo pending test: outgoing last stand chat message?
    public static BuffEnum undyingArmy(String s) {
        return s.contains("Your Undying Army is protecting") ? BuffEnum.UNDYING_ARMY : BuffEnum.NONE;
    }
}
   