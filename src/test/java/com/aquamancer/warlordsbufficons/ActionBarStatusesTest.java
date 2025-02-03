package com.aquamancer.warlordsbufficons;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import com.aquamancer.warlordsbufficons.statuses.ActionBarStatuses;
import com.aquamancer.warlordsbufficons.statuses.Status;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import scala.tools.nsc.interpreter.SimpleMath;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionBarStatusesTest {
    public static Set<Integer> getDeletions(List<Map.Entry<String, Integer>> old, List<Map.Entry<String, Integer>> recent, List<Status> mirrored) {
        return ActionBarStatuses.getDeletions(old, recent, mirrored);
    }
    @Test 
    public void deletions1() {
        List<Map.Entry<String, Integer>> old = new ArrayList<>();
        List<Map.Entry<String, Integer>> recent = new ArrayList<>();
        List<Status> mirrored = new ArrayList<>();
        assertEquals(new HashSet<>(), getDeletions(old, recent, mirrored));
    }
    @Test
    public void deletions2() {
        List<Map.Entry<String, Integer>> old = new ArrayList<>();
        List<Map.Entry<String, Integer>> recent = new ArrayList<>();
        recent.add(new SimpleEntry<>("a", 2));
        List<Status> mirrored = new ArrayList<>();
        assertEquals(new HashSet<>(), getDeletions(old, recent, mirrored));
    }
    @Test
    public void deletions3() {
        List<Map.Entry<String, Integer>> old = new ArrayList<>();
        List<Map.Entry<String, Integer>> recent = new ArrayList<>();
        old.add(new SimpleEntry<>("a", 10));
        recent.add(new SimpleEntry<>("a", 2));
        List<Status> mirrored = new ArrayList<>();
        assertEquals(new HashSet<>(), getDeletions(old, recent, mirrored));
    }
    @Test
    public void deletions4() {
        List<Map.Entry<String, Integer>> old = new ArrayList<>();
        List<Map.Entry<String, Integer>> recent = new ArrayList<>();
        old.add(new SimpleEntry<>("a", 10));
        List<Status> mirrored = new ArrayList<>();
        assertEquals(new HashSet<>(Arrays.asList(0)), getDeletions(old, recent, mirrored));
    }
    @Test
    public void deletions5() {
        List<Map.Entry<String, Integer>> old = new ArrayList<>();
        List<Map.Entry<String, Integer>> recent = new ArrayList<>();
        old.add(new SimpleEntry<>("a", 10));
        old.add(new SimpleEntry<>("a", 10));
        recent.add(new SimpleEntry<>("a", 1));
        recent.add(new SimpleEntry<>("a", 5));
        List<Status> mirrored = new ArrayList<>();
        assertEquals(new HashSet<>(), getDeletions(old, recent, mirrored));
    }
    @Test
    public void deletions6() {
        List<Map.Entry<String, Integer>> old = new ArrayList<>();
        List<Map.Entry<String, Integer>> recent = new ArrayList<>();
        old.add(new SimpleEntry<>("a", 10));
        old.add(new SimpleEntry<>("b", 10));
        recent.add(new SimpleEntry<>("a", 5));
        List<Status> mirrored = new ArrayList<>();
        assertEquals(new HashSet<>(Arrays.asList(1)), getDeletions(old, recent, mirrored));
    }
    @Test
    public void deletions7() {
        List<Map.Entry<String, Integer>> old = new ArrayList<>();
        List<Map.Entry<String, Integer>> recent = new ArrayList<>();
        old.add(new SimpleEntry<>("a", 10));
        old.add(new SimpleEntry<>("b", 10));
        recent.add(new SimpleEntry<>("b", 5));
        List<Status> mirrored = new ArrayList<>();
        assertEquals(new HashSet<>(Arrays.asList(0)), getDeletions(old, recent, mirrored));
    }
    @Test
    public void deletions8() {
        List<Map.Entry<String, Integer>> old = new ArrayList<>();
        List<Map.Entry<String, Integer>> recent = new ArrayList<>();
        old.add(new SimpleEntry<>("a", 10));
        old.add(new SimpleEntry<>("a", 10));
        old.add(new SimpleEntry<>("b", 10));
        recent.add(new SimpleEntry<>("b", 5));
        recent.add(new SimpleEntry<>("b", 5));
        List<Status> mirrored = new ArrayList<>();
        assertEquals(new HashSet<>(Arrays.asList(0, 1)), getDeletions(old, recent, mirrored));
    }
    @Test
    public void deletions9() {
        List<Map.Entry<String, Integer>> old = new ArrayList<>();
        List<Map.Entry<String, Integer>> recent = new ArrayList<>();
        old.add(new SimpleEntry<>("a", 10));
        old.add(new SimpleEntry<>("b", 10));
        old.add(new SimpleEntry<>("c", 10));
        old.add(new SimpleEntry<>("d", 10));
        old.add(new SimpleEntry<>("e", 10));
        old.add(new SimpleEntry<>("f", 10));
        old.add(new SimpleEntry<>("g", 10));
        old.add(new SimpleEntry<>("h", 10));
        recent.add(new SimpleEntry<>("b", 10));
        recent.add(new SimpleEntry<>("c", 10));
        recent.add(new SimpleEntry<>("e", 10));
        recent.add(new SimpleEntry<>("f", 5));
        recent.add(new SimpleEntry<>("h", 5));
        List<Status> mirrored = new ArrayList<>();
        assertEquals(new HashSet<>(Arrays.asList(0, 3, 6)), getDeletions(old, recent, mirrored));
    }
    //todo add cases for partial matches
}
