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
import com.velocitypowered.api.event.scoreboard.ObjectiveEvent;
import com.velocitypowered.api.scoreboard.*;
import com.velocitypowered.proxy.protocol.packet.scoreboard.DisplayObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket.ObjectiveAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class VelocityObjective implements ProxyObjective {

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
        scoreboard.sendPacket(new DisplayObjectivePacket(displaySlot, name));
        scoreboard.getServer().getEventManager().fireAndForget(new ObjectiveEvent.Display(scoreboard.getViewer(), scoreboard, this, displaySlot));
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
    public ProxyScore setScore(@NotNull String holder, @NotNull Consumer<ProxyScore.Builder> consumer) {
        checkState();
        VelocityScore.Builder builder = new VelocityScore.Builder(holder);
        consumer.accept(builder);
        VelocityScore score = scores.get(holder);
        if (score != null) {
            score.updateProperties(builder);
        } else {
            score = builder.build(this);
            scores.put(score.getHolder(), score);
            score.sendUpdate();
        }
        return score;
    }

    @Override
    @Nullable
    public ProxyScore getScore(@NotNull String holder) {
        checkState();
        return scores.get(holder);
    }

    @Override
    public @NotNull Set<ProxyScore> getAllScores() {
        return Set.copyOf(scores.values());
    }

    @Override
    public void removeScore(@NotNull String holder) {
        checkState();
        VelocityScore score = scores.get(holder);
        if (score == null) throw new IllegalArgumentException("Score \"" + holder + "\" is not in this objective (" + name + ")");
        score.remove();
        scores.remove(holder);
    }

    public void sendRegister() {
        scoreboard.sendPacket(new ObjectivePacket(ObjectiveAction.REGISTER, name, title, healthDisplay, numberFormat));
        if (displaySlot != null) {
            scoreboard.sendPacket(new DisplayObjectivePacket(displaySlot, name));
        }
    }

    private void sendUpdate() {
        scoreboard.sendPacket(new ObjectivePacket(ObjectiveAction.UPDATE, name, title, healthDisplay, numberFormat));
    }

    public void unregister() {
        checkState();
        scoreboard.sendPacket(new ObjectivePacket(ObjectiveAction.UNREGISTER, name, title, HealthDisplay.INTEGER, null));
        scoreboard.getServer().getEventManager().fireAndForget(new ObjectiveEvent.Unregister(scoreboard.getViewer(), scoreboard, this));
        registered = false;
    }

    public void clearDisplaySlot() {
        this.displaySlot = null;
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This objective (" + name + ") was unregistered");
    }

    public static class Builder implements ProxyObjective.Builder {

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
        public ProxyObjective.Builder displaySlot(@NotNull DisplaySlot displaySlot) {
            this.displaySlot = displaySlot;
            return this;
        }

        @Override
        @NotNull
        public ProxyObjective.Builder numberFormat(@Nullable NumberFormat numberFormat) {
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
