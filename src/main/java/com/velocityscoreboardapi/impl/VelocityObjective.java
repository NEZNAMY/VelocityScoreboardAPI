package com.velocityscoreboardapi.impl;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.DisplayObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import com.velocityscoreboardapi.api.*;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class VelocityObjective implements Objective {

    @NonNull private final VelocityScoreboard scoreboard;
    @NonNull private final String name;
    @NonNull private Component title;
    @NonNull private HealthDisplay healthDisplay;
    @Nullable private NumberFormat numberFormat;
    @Nullable private DisplaySlot displaySlot;
    private boolean registered = true;
    private final Map<String, VelocityScore> scores = new ConcurrentHashMap<>();

    private VelocityObjective(@NonNull VelocityScoreboard scoreboard, @NonNull String name, @NonNull Component title,
                             @NonNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.title = title;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
    }

    @Override
    public void setDisplaySlot(@NonNull DisplaySlot displaySlot) {
        checkState();
        this.displaySlot = displaySlot;
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new DisplayObjectivePacket(scoreboard.getPriority(), displaySlot.ordinal(), name));
        }
    }

    @Override
    public void setTitle(@NonNull Component title) {
        checkState();
        this.title = title;
        sendUpdate();
    }

    @Override
    public void setHealthDisplay(@NonNull HealthDisplay healthDisplay) {
        checkState();
        this.healthDisplay = healthDisplay;
        sendUpdate();
    }

    @Override
    public void setNumberFormat(@Nullable NumberFormat numberFormat) {
        checkState();
        this.numberFormat = numberFormat;
        sendUpdate();
    }

    @Override
    @NotNull
    public Score findOrCreateScore(@NonNull String name) {
        return findOrCreateScore(name, 0, null, null);
    }

    @Override
    @NotNull
    public Score findOrCreateScore(@NonNull String name, int value, @Nullable Component displayName, @Nullable NumberFormat numberFormat) {
        checkState();
        VelocityScore score = scores.get(name);
        if (score == null) {
            score = new VelocityScore(this, name, value, displayName, numberFormat, true);
            scores.put(name, score);
            score.sendUpdate();
        }
        return score;
    }

    @Override
    public void removeScore(@NonNull String name) {
        checkState();
        VelocityScore score = scores.get(name);
        if (score == null) throw new IllegalArgumentException("Score \"" + name + "\" is not in this objective");
        score.remove();
        scores.remove(name);
    }

    @Override
    public void removeScore(@NonNull Score score) {
        checkState();
        if (!((VelocityScore)score).isRegistered()) throw new IllegalStateException("This score has already been unregistered");
        ((VelocityScore) score).remove();
        scores.remove(score.getHolder());
    }

    public void sendRegister() {
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(title);
        if (legacyTitle.length() > 32) legacyTitle = legacyTitle.substring(0, 32);
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new ObjectivePacket(
                    scoreboard.getPriority(),
                    name,
                    legacyTitle,
                    new ComponentHolder(player.getProtocolVersion(), title),
                    healthDisplay,
                    (byte) 0,
                    numberFormat
            ));
        }
    }

    private void sendUpdate() {
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(title);
        if (legacyTitle.length() > 32) legacyTitle = legacyTitle.substring(0, 32);
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new ObjectivePacket(
                    scoreboard.getPriority(),
                    name,
                    legacyTitle,
                    new ComponentHolder(player.getProtocolVersion(), title),
                    healthDisplay,
                    (byte) 2,
                    numberFormat
            ));
        }
    }

    public void unregister() {
        checkState();
        registered = false;
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new ObjectivePacket(
                    scoreboard.getPriority(),
                    name,
                    "",
                    new ComponentHolder(player.getProtocolVersion(), title),
                    null,
                    (byte) 1,
                    null
            ));
        }
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This objective was unregistered");
    }

    public static class Builder implements Objective.Builder {

        private @NonNull final VelocityScoreboard scoreboard;
        private @NonNull final String name;
        private @NonNull Component title;
        private @NonNull HealthDisplay healthDisplay = HealthDisplay.INTEGER;
        private @Nullable NumberFormat numberFormat = null;

        Builder(@NonNull String name, @NonNull VelocityScoreboard scoreboard) {
            this.scoreboard = scoreboard;
            this.name = name;
            this.title = Component.text(name);
        }

        @Override
        @NotNull
        public Builder title(@NotNull Component title) {
            this.title = title;
            return this;
        }

        @Override
        @NotNull
        public Builder healthDisplay(@NotNull HealthDisplay healthDisplay) {
            this.healthDisplay = healthDisplay;
            return this;
        }

        @Override
        @NotNull
        public Builder numberFormat(@Nullable NumberFormat numberFormat) {
            this.numberFormat = numberFormat;
            return this;
        }

        @Override
        @NotNull
        public VelocityObjective build() {
            if (name.length() > 16) throw new IllegalArgumentException("Objective name cannot be longer than 16 characters (was " + name.length() + ": " + name + ")");
            return new VelocityObjective(scoreboard, name, title, healthDisplay, numberFormat);
        }

    }

}
