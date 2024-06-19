package com.velocityscoreboardapi.impl.downstream;

import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScorePacket;
import com.velocityscoreboardapi.api.DisplaySlot;
import com.velocityscoreboardapi.api.HealthDisplay;
import com.velocityscoreboardapi.api.NumberFormat;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class DownstreamObjective {

    @NonNull private final String objectiveName;
    @Nullable private String titleLegacy;
    @Nullable private ComponentHolder titleModern;
    @Nullable private HealthDisplay healthDisplay;
    @Nullable private NumberFormat numberFormat;
    @Nullable @Setter private DisplaySlot displaySlot;
    @NotNull private final Map<String, DownstreamScore> scores = new ConcurrentHashMap<>();

    public void update(@NonNull ObjectivePacket packet) {
        titleLegacy = packet.getTitleLegacy();
        titleModern = packet.getTitleModern();
        healthDisplay = packet.getHealthDisplay();
        numberFormat = packet.getNumberFormat();
    }

    public void setScore(@NonNull ScorePacket packet) {
        if (scores.containsKey(packet.getScoreHolder())) {
            scores.get(packet.getScoreHolder()).update(packet);
        } else {
            scores.put(packet.getScoreHolder(), new DownstreamScore(packet.getScoreHolder(), packet.getValue(), packet.getDisplayName(), packet.getNumberFormat()));
        }
    }

    public void removeScore(@NonNull String holder) {
        scores.remove(holder);
    }
}
