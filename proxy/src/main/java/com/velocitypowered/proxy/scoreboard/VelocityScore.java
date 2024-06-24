/*
 * This file is part of VelocityScoreboardAPI, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) NEZNAMY <n.e.z.n.a.m.y@azet.sk>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.velocitypowered.proxy.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.api.scoreboard.Score;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScorePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScoreResetPacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScoreSetPacket;
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
        if (this.score == score) return;
        this.score = score;
        sendUpdate();
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        if (!registered) throw new IllegalStateException("This score was unregistered");
        if (this.displayName == displayName) return;
        this.displayName = displayName;
        sendUpdate();
    }

    @Override
    public void setNumberFormat(@Nullable NumberFormat numberFormat) {
        if (!registered) throw new IllegalStateException("This score was unregistered");
        if (this.numberFormat == numberFormat) return;
        this.numberFormat = numberFormat;
        sendUpdate();
    }

    public void sendUpdate() {
        if (objective.getScoreboard().getViewer().getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            ComponentHolder cHolder = displayName == null ? null : new ComponentHolder(objective.getScoreboard().getViewer().getProtocolVersion(), displayName);
            objective.getScoreboard().sendPacket(new ScoreSetPacket(holder, objective.getName(), score, cHolder, numberFormat));
        } else {
            objective.getScoreboard().sendPacket(new ScorePacket(ScorePacket.ScoreAction.SET, holder, objective.getName(), score));
        }
    }

    public void sendRemove() {
        if (objective.getScoreboard().getViewer().getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
            objective.getScoreboard().sendPacket(new ScoreResetPacket(holder, objective.getName()));
        } else {
            objective.getScoreboard().sendPacket(new ScorePacket(ScorePacket.ScoreAction.RESET, holder, objective.getName(), 0));
        }
    }

    public void remove() {
        if (!registered) throw new IllegalStateException("This score was unregistered");
        registered = false;
        sendRemove();
    }

    public static class Builder implements Score.Builder {

        @NotNull private final String holder;
        private int score;
        @Nullable private Component displayName;
        @Nullable private NumberFormat numberFormat = null;

        public Builder(@NotNull String holder) {
            this.holder = holder;
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

        /**
         * Builds this score.
         *
         * @param   objective
         *          Objective to build this score into
         * @return  New built score
         */
        @NotNull
        public VelocityScore build(@NotNull VelocityObjective objective) {
            return new VelocityScore(objective, holder, score, displayName, numberFormat);
        }
    }
}
