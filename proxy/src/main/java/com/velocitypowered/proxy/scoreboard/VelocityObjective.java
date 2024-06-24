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

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.scoreboard.*;
import com.velocitypowered.proxy.protocol.packet.scoreboard.DisplayObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket.ObjectiveAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VelocityObjective implements Objective {

    @NotNull private final VelocityScoreboard scoreboard;
    @NotNull private final String name;
    @NotNull private TextHolder title;
    @NotNull private HealthDisplay healthDisplay;
    @Nullable private NumberFormat numberFormat;
    @Nullable private DisplaySlot displaySlot;
    private boolean registered = true;
    private final Map<String, VelocityScore> scores = new ConcurrentHashMap<>();

    private VelocityObjective(@NotNull VelocityScoreboard scoreboard, @NotNull String name, @NotNull TextHolder title,
                             @NotNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat, @Nullable DisplaySlot displaySlot) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.title = title;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
        this.displaySlot = displaySlot;
        if (displaySlot != null) scoreboard.setDisplaySlot(displaySlot, this);
    }

    @NotNull
    public VelocityScoreboard getScoreboard() {
        return scoreboard;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public TextHolder getTitle() {
        return title;
    }

    @NotNull
    @Override
    public HealthDisplay getHealthDisplay() {
        return healthDisplay;
    }

    @Nullable
    @Override
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    @Nullable
    @Override
    public DisplaySlot getDisplaySlot() {
        return displaySlot;
    }

    @Override
    public void setDisplaySlot(@NotNull DisplaySlot displaySlot) {
        checkState();
        if (this.displaySlot == displaySlot) return;
        scoreboard.setDisplaySlot(displaySlot, this);
        this.displaySlot = displaySlot;
        scoreboard.getViewer().getConnection().write(new DisplayObjectivePacket(displaySlot, name));
    }

    @Override
    public void setTitle(@NotNull TextHolder title) {
        checkState();
        if (this.title == title) return;
        this.title = title;
        sendUpdate();
    }

    @Override
    public void setHealthDisplay(@NotNull HealthDisplay healthDisplay) {
        checkState();
        if (this.healthDisplay == healthDisplay) return;
        this.healthDisplay = healthDisplay;
        sendUpdate();
    }

    @Override
    public void setNumberFormat(@Nullable NumberFormat numberFormat) {
        checkState();
        if (this.numberFormat == numberFormat) return;
        this.numberFormat = numberFormat;
        sendUpdate();
    }

    @Override
    @NotNull
    public Score.Builder scoreBuilder(@NotNull String holder) {
        return new VelocityScore.Builder(holder);
    }

    @Override
    @NotNull
    public Score registerScore(@NotNull Score.Builder builder) {
        checkState();
        VelocityScore score = ((VelocityScore.Builder)builder).build(this);
        scores.put(score.getHolder(), score);
        score.sendUpdate();
        return score;
    }

    @Override
    @Nullable
    public Score getScore(@NotNull String holder) {
        checkState();
        return scores.get(holder);
    }

    @Override
    public void removeScore(@NotNull String holder) {
        checkState();
        VelocityScore score = scores.get(holder);
        if (score == null) throw new IllegalArgumentException("Score \"" + holder + "\" is not in this objective");
        score.remove();
        scores.remove(holder);
    }

    public void sendRegister() {
        scoreboard.getViewer().getConnection().write(new ObjectivePacket(ObjectiveAction.REGISTER, name, title, healthDisplay, numberFormat));
        if (displaySlot != null) {
            scoreboard.getViewer().getConnection().write(new DisplayObjectivePacket(displaySlot, name));
        }
    }

    private void sendUpdate() {
        scoreboard.getViewer().getConnection().write(new ObjectivePacket(ObjectiveAction.UPDATE, name, title, healthDisplay, numberFormat));
    }

    public void sendUnregister() {
        scoreboard.getViewer().getConnection().write(new ObjectivePacket(ObjectiveAction.UNREGISTER, name, title, HealthDisplay.INTEGER, null));
    }

    public void unregister() {
        checkState();
        registered = false;
        sendUnregister();
    }

    public void clearDisplaySlot() {
        this.displaySlot = null;
    }

    @NotNull
    public Collection<VelocityScore> getScores() {
        return scores.values();
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This objective was unregistered");
    }

    public static class Builder implements Objective.Builder {

        @NotNull private final String name;
        @NotNull private TextHolder title;
        @NotNull private HealthDisplay healthDisplay = HealthDisplay.INTEGER;
        @Nullable private DisplaySlot displaySlot = null;
        @Nullable private NumberFormat numberFormat = null;

        public Builder(@NotNull String name) {
            if (name.length() > 16) throw new IllegalArgumentException("Objective name cannot be longer than 16 characters (was " + name.length() + ": " + name + ")");
            this.name = name;
            this.title = new TextHolder(name);
        }

        @Override
        @NotNull
        public Builder title(@NotNull TextHolder title) {
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
        public Objective.Builder displaySlot(@NotNull DisplaySlot displaySlot) {
            this.displaySlot = displaySlot;
            return this;
        }

        @Override
        @NotNull
        public Objective.Builder numberFormat(@Nullable NumberFormat numberFormat) {
            this.numberFormat = numberFormat;
            return this;
        }

        /**
         * Builds this objective.
         *
         * @param   scoreboard
         *          Scoreboard to register into
         * @return  Objective from builder
         */
        @NotNull
        public VelocityObjective build(@NotNull VelocityScoreboard scoreboard) {
            return new VelocityObjective(scoreboard, name, title, healthDisplay, numberFormat, displaySlot);
        }
    }

}
