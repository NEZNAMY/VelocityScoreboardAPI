package com.velocityscoreboardapi.impl;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScorePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScoreResetPacket;
import com.velocityscoreboardapi.api.NumberFormat;
import com.velocityscoreboardapi.api.Objective;
import com.velocityscoreboardapi.api.Score;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VelocityScore implements Score {

    @NonNull private final VelocityObjective objective;
    @NonNull private final String holder;
    private int score;
    @Nullable private Component displayName;
    @Nullable private NumberFormat numberFormat;
    private boolean registered;

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
            ComponentHolder cHolder = displayName == null ? null : new ComponentHolder(player.getProtocolVersion(), displayName);
            player.getConnection().write(new ScorePacket(objective.getScoreboard().getPriority(), ScorePacket.ScoreAction.SET, holder, objective.getName(), score, cHolder, numberFormat));
        }
    }

    public void remove() {
        if (!registered) throw new IllegalStateException("This score was unregistered");
        registered = false;
        for (ConnectedPlayer player : objective.getScoreboard().getPlayers()) {
            if (player.getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                player.getConnection().write(new ScoreResetPacket(objective.getScoreboard().getPriority(), holder, objective.getName()));
            } else {
                player.getConnection().write(new ScorePacket(objective.getScoreboard().getPriority(), ScorePacket.ScoreAction.RESET, holder, objective.getName(),
                        0, null, null));
            }
        }
    }

    public static class Builder implements Score.Builder {

        private String holder;
        private int score;
        private Component displayName;
        private NumberFormat numberFormat;

        public Builder(@NonNull String holder) {
            this.holder = holder;
        }

        @Override
        @NotNull
        public Score.Builder holder(@NonNull String holder) {
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
        public Score build(@NonNull Objective objective) {
            return new VelocityScore((VelocityObjective) objective, holder, score, displayName, numberFormat, true);
        }

    }

}
