package com.aquamancer.warlordsbufficons.handlers;

import com.aquamancer.warlordsbufficons.statuses.BuffEnum;
import com.aquamancer.warlordsbufficons.statuses.DebuffEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Provides methods that identify whether a String(chat message) indicates an event that affects buffs/debuffs.
 */
public class ChatAbilityIdentifiers {
    public static List<DebuffEnum> getDebuffMatches(String s) {
        // gather all methods into a List
        List<Supplier<DebuffEnum>> operations = new ArrayList<>();
        operations.add(() -> slowedFrostbolt(s));
        operations.add(() -> slowedFreezingBreath(s));
        operations.add(() -> wounded(s));
        operations.add(() -> crippled(s));
        operations.add(() -> undyingArmyPopped(s));
        
        // collect all debuff matches
        List<DebuffEnum> result = new ArrayList<>();
        for (Supplier<DebuffEnum> operation : operations) {
            // add the Debuff match to result if the method call does not return NONE
            DebuffEnum value = operation.get(); // evaluate the method
            if (value != DebuffEnum.NONE)
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
    private static DebuffEnum slowedFrostbolt(String s) {
        return s.contains("Frostbolt hit you") ? DebuffEnum.SLOW_FROSTBOLT : DebuffEnum.NONE;
    }
    private static DebuffEnum slowedFreezingBreath(String s) {
        return s.contains("Freezing Breath hit you") ? DebuffEnum.SLOW_FREEZING_BREATH : DebuffEnum.NONE;
    }
    // aquamancer
    
    // WARRIOR
    // bers
    public static DebuffEnum wounded(String s) {
        return s.contains("Wounding Strike hit you") ? DebuffEnum.WOUNDED : DebuffEnum.NONE;
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
   