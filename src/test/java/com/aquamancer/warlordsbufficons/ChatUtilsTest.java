package com.aquamancer.warlordsbufficons;

import com.aquamancer.warlordsbufficons.chat.ChatUtils;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatUtilsTest {
    private static final String S = "§";
    private static void parseFormattedChatMessage(String message, List<Map.Entry<String, List<Character>>> list) {
        ChatUtils.parseFormattedChatMessage(message, list);
    }
    
    private static List<Character> chars(Character... ch) {
        return new ArrayList<>(Arrays.asList(ch));
    }
    private static Map.Entry<String, List<Character>> entry(String s, Character... ch) {
        return new SimpleEntry<>(s, chars(ch));
    }
    
    @Test
    public void parseEmptyString() {
        List<Map.Entry<String, List<Character>>> t = new ArrayList<>();
        parseFormattedChatMessage("", t);
        assertEquals(new ArrayList<>(), t);
    }
    @Test
    public void parseNoFormatting() {
        List<Map.Entry<String, List<Character>>> t = new ArrayList<>();
        List<Map.Entry<String, List<Character>>> exp = new ArrayList<>();
        parseFormattedChatMessage("nerf protector", t);

        exp.add(new SimpleEntry<>("nerf protector", chars()));
        assertEquals(exp, t);
    }
    @Test
    public void parseNoFormattingAndFormatting() {
        List<Map.Entry<String, List<Character>>> t = new ArrayList<>();
        List<Map.Entry<String, List<Character>>> exp = new ArrayList<>();
        parseFormattedChatMessage("nerf " + S + "1protector", t);

        exp.add(entry("nerf "));
        exp.add(entry("protector", '1'));
        assertEquals(exp, t);
    }
    @Test
    public void parseOnlyFormatting() {
        List<Map.Entry<String, List<Character>>> t = new ArrayList<>();
        List<Map.Entry<String, List<Character>>> exp = new ArrayList<>();
        parseFormattedChatMessage(S + "1protector", t);

        exp.add(entry("protector", '1'));
        assertEquals(exp, t);
    }
    @Test
    public void parseMultiFormatting() {
        List<Map.Entry<String, List<Character>>> t = new ArrayList<>();
        List<Map.Entry<String, List<Character>>> exp = new ArrayList<>();
        parseFormattedChatMessage(S + "1" + S + "9" + "protector", t);

        exp.add(entry("protector", '1', '9'));
        assertEquals(exp, t);
    }
    @Test
    public void parseMultiMultiFormatting() {
        List<Map.Entry<String, List<Character>>> t = new ArrayList<>();
        List<Map.Entry<String, List<Character>>> exp = new ArrayList<>();
        parseFormattedChatMessage(S + "1" + S + "9" + "protector" + S + "z" + S + "3sucks", t);

        exp.add(entry("protector", '1','9'));
        exp.add(entry("sucks", 'z','3'));
        assertEquals(exp, t);
    }
    @Test
    public void parseRealisticLINF() {
        List<Map.Entry<String, List<Character>>> t = new ArrayList<>();
        List<Map.Entry<String, List<Character>>> exp = new ArrayList<>();
        parseFormattedChatMessage("§aLINF§7:§61", t);
        exp.add(entry("LINF", 'a'));
        exp.add(entry(":", '7'));
        exp.add(entry("1", '6'));
        assertEquals(exp, t);
    }
    @Test
    public void parseRealisticWND() {
        List<Map.Entry<String, List<Character>>> t = new ArrayList<>();
        List<Map.Entry<String, List<Character>>> exp = new ArrayList<>();
        parseFormattedChatMessage("§cWND§7:§63", t);
        exp.add(entry("WND", 'c'));
        exp.add(entry(":", '7'));
        exp.add(entry("3", '6'));
        assertEquals(exp, t);
    }
}
