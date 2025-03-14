package com.aquamancer.warlordsbufficons.chat;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;

public class ChatUtils {
    /**
     * Parses a formatted String, where format characters are preceded by §, and adds
     * (substring, list of format chars for substring) to list
     * @updates list
     * @param message the string to be parsed
     * @param list List of (substring, list of format characters for substring)
     */
    public static void parseFormattedChatMessage(String message, List<Map.Entry<String, Set<Character>>> list) {
        // base case: message is empty string
        if (message.isEmpty() || message.equals("§")) return; // or message.charAt(message.length() - 1) == '§'
        if (message.charAt(0) != '§') { // if the message begins without any formatting codes
            int indexOfNextSymbol = message.indexOf('§');
            int startOfNextSubstring = indexOfNextSymbol != -1 ? indexOfNextSymbol : message.length();
            list.add(new SimpleImmutableEntry<>(message.substring(0, startOfNextSubstring), new HashSet<>()));
            parseFormattedChatMessage(message.substring(startOfNextSubstring), list);
        } else {
            Set<Character> formatCodes = new HashSet<>();
            int i;
            for (i = 0; i + 1 < message.length() && message.charAt(i) == '§'; i += 2) {
                formatCodes.add(message.charAt(i + 1));
            }
            // i is now the index of <last consecutive symbol + 2> unless message is just '§'
            int indexOfNextSymbol = message.indexOf('§', i);
            int startOfNextSubstring = indexOfNextSymbol != -1 ? indexOfNextSymbol : message.length();
            list.add(new SimpleImmutableEntry<>(message.substring(i, startOfNextSubstring), formatCodes));
            parseFormattedChatMessage(message.substring(startOfNextSubstring), list);
        }
    }
    public static boolean isWarlordsActionBar(String formattedText) {
        // BEGIN:               §r               §6§lHP: §e§l2370§6§l/6152§r     §9§lBLU Team§r    §aLINF§7:§61 §cCRIP§7:§63 §cWND§7:§63 §r§r
        List<Map.Entry<String, Set<Character>>> components = new ArrayList<>();
        parseFormattedChatMessage(formattedText, components);
        
        for (int i = 0; i < components.size() - 4; i++) {
            if (
                    components.get(i).getKey().equals("HP: ")
                    && components.get(i + 1).getKey().matches("\\d+")
                    && components.get(i + 2).getKey().matches("/\\d+")
                    && components.get(i + 4).getKey().contains("Team")
            ) return true;
        }
        return false;
    }
}
