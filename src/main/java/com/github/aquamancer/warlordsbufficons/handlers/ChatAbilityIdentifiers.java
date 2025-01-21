package com.github.aquamancer.warlordsbufficons.handlers;

import com.github.aquamancer.warlordsbufficons.statuses.Buff;
import com.github.aquamancer.warlordsbufficons.statuses.Debuff;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Provides methods that identify whether a String(chat message) indicates an event that affects buffs/debuffs.
 */
public class ChatAbilityIdentifiers {
    public static List<Debuff> getDebuffMatches(String s) {
        // gather all methods into a List
        List<Supplier<Debuff>> operations = new ArrayList<>();
        operations.add(() -> slowedFrostbolt(s));
        operations.add(() -> slowedFreezingBreath(s));
        operations.add(() -> wounded(s));
        operations.add(() -> crippled(s));
        operations.add(() -> undyingArmyPopped(s));
        
        // collect all debuff matches
        List<Debuff> result = new ArrayList<>();
        for (Supplier<Debuff> operation : operations) {
            // add the Debuff match to result if the method call does not return NONE
            Debuff value = operation.get(); // evaluate the method
            if (value != Debuff.NONE)
                result.add(value);
        }
        return result;
    }
    public static List<Buff> getBuffMatches(String s) {
        // gather all methods into a List
        List<Supplier<Buff>> operations = new ArrayList<>();
        operations.add(() -> interveneIncoming(s));
        operations.add(() -> interveneOutgoing(s));
        operations.add(() -> lastStandIncoming(s));
        operations.add(() -> undyingArmy(s));
        
        // collect all buff matches
        List<Buff> result = new ArrayList<>();
        for (Supplier<Buff> operation : operations) {
            // add the Buff match to result if the method call does not return NONE
            Buff value = operation.get(); // evaluate the method
            if (value != Buff.NONE)
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
    private static Debuff slowedFrostbolt(String s) {
        return s.contains("Frostbolt hit you") ? Debuff.SLOW_FROSTBOLT : Debuff.NONE;
    }
    private static Debuff slowedFreezingBreath(String s) {
        return s.contains("Freezing Breath hit you") ? Debuff.SLOW_FREEZING_BREATH : Debuff.NONE;
    }
    // aquamancer
    
    // WARRIOR
    // bers
    public static Debuff wounded(String s) {
        return s.contains("Wounding Strike hit you") ? Debuff.WOUNDED : Debuff.NONE;
    }
    // rev
    public static Debuff crippled(String s) {
        return s.contains("Crippling Strike hit you") ? Debuff.CRIPPLED : Debuff.NONE;
    }
    public static Debuff undyingArmyPopped(String s) {
        return s.contains("Undying Army revived you with temporary health") ? Debuff.UNDYING_ARMY_POPPED : Debuff.NONE;
    }
    
    /*
        outgoing effects (chat messages with the bit right shift character)
     */
    public static Buff interveneIncoming(String s) {
        return s.contains("is shielding you with their Intervene") ? Buff.INTERVENE_INCOMING : Buff.NONE;
    }
    public static Buff interveneOutgoing(String s) {
        return s.contains("with your Intervene") ? Buff.INTERVENE_OUTGOING : Buff.NONE;
    }
    public static Buff lastStandIncoming(String s) {
        return s.contains("Last Stand is protecting you") ? Buff.LAST_STAND_INCOMING : Buff.NONE;
    }
    // todo pending test: outgoing last stand chat message?
    public static Buff undyingArmy(String s) {
        return s.contains("Your Undying Army is protecting") ? Buff.UNDYING_ARMY : Buff.NONE;
    }
}
   