package net.craftersland.ctw.server.database;

import java.util.UUID;

public class CTWPlayer {

    private UUID uuid;
    private String name;
    private String effects;
    private int score;
    private int totalKills;
    private int matchTotalKills;
    private int defenseKills;
    private int matchDefenseKills;
    private int meleeKills;
    private int matchMeleeKills;
    private int bowKills;
    private int matchBowKills;
    private int bowDistanceKill;
    private int woolPickups;
    private int woolPlacements;
    private long lastSeen;

    public CTWPlayer(UUID uuid, String name, int score, int totalKills, int defenseKills, int meleeKills, int bowKills, int bowDistanceKill, int woolPickups, int woolPlacements) {
        this.uuid = uuid;
        this.name = name;
        this.score = score;
        this.totalKills = totalKills;
        this.defenseKills = defenseKills;
        this.meleeKills = meleeKills;
        this.bowKills = bowKills;
        this.bowDistanceKill = bowDistanceKill;
        this.woolPickups = woolPickups;
        this.woolPlacements = woolPlacements;
        this.lastSeen = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getEffects() {
        return effects;
    }

    public int getScore() {
        return score;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getMatchTotalKills() {
        return matchTotalKills;
    }

    public int getMeleeKills() {
        return meleeKills;
    }

    public int getMatchMeleeKills() {
        return matchMeleeKills;
    }

    public int getDefenseKills() {
        return defenseKills;
    }

    public int getMatchDefenseKills() {
        return matchDefenseKills;
    }

    public int getBowKills() {
        return bowKills;
    }

    public int getMatchBowKills() {
        return matchBowKills;
    }

    public int getBowDistanceKill() {
        return bowDistanceKill;
    }

    public int getWoolPickups() {
        return woolPickups;
    }

    public int getWoolPlacements() {
        return woolPlacements;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEffects(String effects) {
        this.effects = effects;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }

    public void setMatchTotalKills(int matchTotalKills) {
        this.matchTotalKills = matchTotalKills;
    }

    public void setDefenseKills(int defenseKills) {
        this.defenseKills = defenseKills;
    }

    public void setMatchDefenseKills(int matchDefenseKills) {
        this.matchDefenseKills = matchDefenseKills;
    }

    public void setMeleeKills(int meleeKills) {
        this.meleeKills = meleeKills;
    }

    public void setMatchMeleeKills(int matchMeleeKills) {
        this.matchMeleeKills = matchMeleeKills;
    }

    public void setBowKills(int bowKills) {
        this.bowKills = bowKills;
    }

    public void setMatchBowKills(int matchBowKills) {
        this.matchBowKills = matchBowKills;
    }

    public void setBowDistanceKill(int bowDistanceKill) {
        this.bowDistanceKill = bowDistanceKill;
    }

    public void setWoolPickups(int woolPickups) {
        this.woolPickups = woolPickups;
    }

    public void setWoolPlacements(int woolPlacements) {
        this.woolPlacements = woolPlacements;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}
