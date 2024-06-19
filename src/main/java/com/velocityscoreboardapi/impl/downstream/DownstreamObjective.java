package com.velocityscoreboardapi.impl.downstream;

import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScorePacket;
import com.velocityscoreboardapi.api.DisplaySlot;
import com.velocityscoreboardapi.api.HealthDisplay;
import com.velocityscoreboardapi.api.NumberFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownstreamObjective {

    @NotNull private final String objectiveName;
    @Nullable private String titleLegacy;
    @Nullable private ComponentHolder titleModern;
    @Nullable private HealthDisplay healthDisplay;
    @Nullable private NumberFormat numberFormat;
    @Nullable private DisplaySlot displaySlot;
    @NotNull private final Map<String, DownstreamScore> scores = new ConcurrentHashMap<>();

    public DownstreamObjective(@NotNull String objectiveName, @Nullable String titleLegacy, @Nullable ComponentHolder titleModern,
                               @Nullable HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat, @Nullable DisplaySlot displaySlot) {
        this.objectiveName = objectiveName;
        this.titleLegacy = titleLegacy;
        this.titleModern = titleModern;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
        this.displaySlot = displaySlot;
    }

    public void setDisplaySlot(@NotNull DisplaySlot displaySlot) {
        this.displaySlot = displaySlot;
    }

    public void update(@NotNull ObjectivePacket packet) {
        titleLegacy = packet.getTitleLegacy();
        titleModern = packet.getTitleModern();
        healthDisplay = packet.getHealthDisplay();
        numberFormat = packet.getNumberFormat();
    }

    public void setScore(@NotNull ScorePacket packet) {
        if (scores.containsKey(packet.getScoreHolder())) {
            scores.get(packet.getScoreHolder()).update(packet);
        } else {
            scores.put(packet.getScoreHolder(), new DownstreamScore(packet.getScoreHolder(), packet.getValue(), packet.getDisplayName(), packet.getNumberFormat()));
        }
    }

    public void removeScore(@NotNull String holder) {
        scores.remove(holder);
    }
}
