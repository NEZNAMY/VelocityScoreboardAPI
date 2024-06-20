package com.velocityscoreboardapi.impl;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScorePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScoreResetPacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScoreSetPacket;
import com.velocityscoreboardapi.api.NumberFormat;
import com.velocityscoreboardapi.api.Objective;
import com.velocityscoreboardapi.api.Score;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VelocityScore implements Score {

    @NotNull private final VelocityObjective objective;
    @NotNull private final String holder;
    private int score;
    @Nullable private Component displayName;
    @Nullable private NumberFormat numberFormat;
    private boolean registered = true;

    private VelocityScore(@NotNull VelocityObjective objective, @NotNull String holder, int score,
                         @Nullable Component displayName, @Nullable NumberFormat numberFormat) {
        this.objective = objective;
        this.holder = holder;
        this.score = score;
        this.displayName = displayName;
        this.numberFormat = numberFormat;
    }

    @NotNull
    @Override
    public String getHolder() {
        return holder;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Nullable
    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Nullable
    @Override
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public void setScore(int score) {
        if (!registered) throw new IllegalStateException("This score was unregistered");
        this.score = score;
        sendUpdate();
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        if (!registered) throw new IllegalStateException("This score was unregistered");
        this.displayName = displayName;
        sendUpdate();
    }

    @Override
    public void setNumberFormat(@Nullable NumberFormat numberFormat) {
        if (!registered) throw new IllegalStateException("This score was unregistered");
        this.numberFormat = numberFormat;
        sendUpdate();
    }

    public void sendUpdate() {
        for (ConnectedPlayer player : objective.getScoreboard().getPlayers()) {
            if (player.getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                ComponentHolder cHolder = displayName == null ? null : new ComponentHolder(player.getProtocolVersion(), displayName);
                player.getConnection().write(new ScoreSetPacket(objective.getScoreboard().getPriority(), holder, objective.getName(), score, cHolder, numberFormat));
            } else {
                player.getConnection().write(new ScorePacket(objective.getScoreboard().getPriority(), ScorePacket.ScoreAction.SET, holder, objective.getName(), score));
            }
        }
    }

    public void remove() {
        if (!registered) throw new IllegalStateException("This score was unregistered");
        registered = false;
        for (ConnectedPlayer player : objective.getScoreboard().getPlayers()) {
            if (player.getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                player.getConnection().write(new ScoreResetPacket(objective.getScoreboard().getPriority(), holder, objective.getName()));
            } else {
                player.getConnection().write(new ScorePacket(objective.getScoreboard().getPriority(), ScorePacket.ScoreAction.RESET, holder, objective.getName(), 0));
            }
        }
    }

    public static class Builder implements Score.Builder {

        private String holder;
        private int score;
        private Component displayName;
        private NumberFormat numberFormat;

        public Builder(@NotNull String holder) {
            this.holder = holder;
        }

        @Override
        @NotNull
        public Score.Builder holder(@NotNull String holder) {
            this.holder = holder;
            return this;
        }

        @Override
        @NotNull
        public Score.Builder score(int score) {
            this.score = score;
            return this;
        }

        @Override
        @NotNull
        public Score.Builder displayName(@Nullable Component displayName) {
            this.displayName = displayName;
            return this;
        }

        @Override
        @NotNull
        public Score.Builder numberFormat(@Nullable NumberFormat numberFormat) {
            this.numberFormat = numberFormat;
            return this;
        }

        @Override
        @NotNull
        public Score build(@NotNull Objective objective) {
            return new VelocityScore((VelocityObjective) objective, holder, score, displayName, numberFormat);
        }

    }

}
