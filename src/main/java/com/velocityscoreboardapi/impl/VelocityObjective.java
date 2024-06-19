package com.velocityscoreboardapi.impl;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.DisplayObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import com.velocityscoreboardapi.api.*;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class VelocityObjective implements Objective {

    @NotNull private final VelocityScoreboard scoreboard;
    @NotNull private final String name;
    @NotNull private Component title;
    @NotNull private HealthDisplay healthDisplay;
    @Nullable private NumberFormat numberFormat;
    @Nullable private DisplaySlot displaySlot;
    private boolean registered = true;
    private final Map<String, VelocityScore> scores = new ConcurrentHashMap<>();

    private VelocityObjective(@NotNull VelocityScoreboard scoreboard, @NotNull String name, @NotNull Component title,
                             @NotNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.title = title;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
    }

    @Override
    public void setDisplaySlot(@NotNull DisplaySlot displaySlot) {
        checkState();
        this.displaySlot = displaySlot;
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new DisplayObjectivePacket(scoreboard.getPriority(), displaySlot, name));
        }
    }

    @Override
    public void setTitle(@NotNull Component title) {
        checkState();
        this.title = title;
        sendUpdate();
    }

    @Override
    public void setHealthDisplay(@NotNull HealthDisplay healthDisplay) {
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
    public Score findOrCreateScore(@NotNull String name, @NotNull Score.Builder builder) {
        checkState();
        VelocityScore score = scores.get(name);
        if (score == null) {
            score = (VelocityScore) builder.build(this);
            scores.put(name, score);
            score.sendUpdate();
        }
        return score;
    }

    @Override
    public void removeScore(@NotNull String name) {
        checkState();
        VelocityScore score = scores.get(name);
        if (score == null) throw new IllegalArgumentException("Score \"" + name + "\" is not in this objective");
        score.remove();
        scores.remove(name);
    }

    @Override
    public void removeScore(@NotNull Score score) {
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
                    ObjectivePacket.ObjectiveAction.REGISTER,
                    name,
                    legacyTitle,
                    new ComponentHolder(player.getProtocolVersion(), title),
                    healthDisplay,
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
                    ObjectivePacket.ObjectiveAction.UPDATE,
                    name,
                    legacyTitle,
                    new ComponentHolder(player.getProtocolVersion(), title),
                    healthDisplay,
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
                    ObjectivePacket.ObjectiveAction.UNREGISTER,
                    name,
                    "",
                    new ComponentHolder(player.getProtocolVersion(), title),
                    null,
                    null
            ));
        }
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This objective was unregistered");
    }

    public static class Builder implements Objective.Builder {

        private String name;
        private Component title;
        @NotNull private HealthDisplay healthDisplay = HealthDisplay.INTEGER;
        @Nullable private NumberFormat numberFormat = null;

        @Override
        @NotNull
        public Objective.Builder name(@NotNull String name) {
            this.name = name;
            if (this.title == null) {
                this.title = Component.text(name);
            }
            return this;
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
        public VelocityObjective build(@NotNull Scoreboard scoreboard) {
            if (name.length() > 16) throw new IllegalArgumentException("Objective name cannot be longer than 16 characters (was " + name.length() + ": " + name + ")");
            return new VelocityObjective((VelocityScoreboard) scoreboard, name, title, healthDisplay, numberFormat);
        }

    }

}
