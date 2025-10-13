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
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Getter
public class VelocityObjective implements ProxyObjective {

    @NonNull private final VelocityScoreboard scoreboard;
    @NonNull private final String name;
    @NonNull private TextHolder title;
    @NonNull private HealthDisplay healthDisplay;
    @Nullable private NumberFormat numberFormat;
    @Nullable private DisplaySlot displaySlot;
    private boolean registered = true;
    private final Map<String, VelocityScore> scores = new ConcurrentHashMap<>();

    private VelocityObjective(@NonNull VelocityScoreboard scoreboard, @NonNull String name, @NonNull TextHolder title,
                             @NonNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat, @Nullable DisplaySlot displaySlot) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.title = title;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
        this.displaySlot = displaySlot;
        if (displaySlot != null) scoreboard.setDisplaySlot(displaySlot, this);
    }

    @Override
    public void setDisplaySlot(@NonNull DisplaySlot displaySlot) {
        checkState();
        if (this.displaySlot == displaySlot) return;
        scoreboard.setDisplaySlot(displaySlot, this);
        this.displaySlot = displaySlot;
        scoreboard.sendPacket(new DisplayObjectivePacket(displaySlot, name));
        scoreboard.getEventSource().fireEvent(new ObjectiveEvent.Display(scoreboard.getViewer(), scoreboard, this, displaySlot));
    }

    @Override
    public void setTitle(@NonNull TextHolder title) {
        checkState();
        if (this.title == title) return;
        this.title = title;
        sendUpdate();
    }

    @Override
    public void setHealthDisplay(@NonNull HealthDisplay healthDisplay) {
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
    public ProxyScore setScore(@NonNull String holder, @NonNull Consumer<ProxyScore.Builder> consumer) {
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
    public ProxyScore getScore(@NonNull String holder) {
        checkState();
        return scores.get(holder);
    }

    @Override
    @NotNull
    public Collection<ProxyScore> getAllScores() {
        return Collections.unmodifiableCollection(scores.values());
    }

    @Override
    public void removeScore(@NonNull String holder) {
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
        scoreboard.getEventSource().fireEvent(new ObjectiveEvent.Unregister(scoreboard.getViewer(), scoreboard, this));
        registered = false;
    }

    public void clearDisplaySlot() {
        this.displaySlot = null;
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This objective (" + name + ") was unregistered");
    }

    /**
     * Creates a dump of this objective into a list of lines.
     *
     * @return  dump of this objective
     */
    @NotNull
    public List<String> dump() {
        List<String> content = new ArrayList<>();
        content.add("  " + name + ":");
        content.add("    Title: " + title);
        content.add("    HealthDisplay: " + healthDisplay);
        content.add("    NumberFormat: " + numberFormat);
        content.add("    DisplaySlot: " + displaySlot);
        content.add("    Scores (" + scores.size() + "):");
        for (VelocityScore score : scores.values()) {
            content.addAll(score.dump());
        }
        return content;
    }

    public static class Builder implements ProxyObjective.Builder {

        @NonNull private final String name;
        @NonNull private TextHolder title;
        @NonNull private HealthDisplay healthDisplay = HealthDisplay.INTEGER;
        @Nullable private DisplaySlot displaySlot = null;
        @Nullable private NumberFormat numberFormat = null;

        public Builder(@NonNull String name) {
            if (name.length() > 16) throw new IllegalArgumentException("Objective name cannot be longer than 16 characters (was " + name.length() + ": " + name + ")");
            this.name = name;
            this.title = TextHolder.of(name);
        }

        @Override
        @NotNull
        public Builder title(@NonNull TextHolder title) {
            this.title = title;
            return this;
        }

        @Override
        @NotNull
        public Builder healthDisplay(@NonNull HealthDisplay healthDisplay) {
            this.healthDisplay = healthDisplay;
            return this;
        }

        @Override
        @NotNull
        public ProxyObjective.Builder displaySlot(@NonNull DisplaySlot displaySlot) {
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
        public VelocityObjective build(@NonNull VelocityScoreboard scoreboard) {
            return new VelocityObjective(scoreboard, name, title, healthDisplay, numberFormat, displaySlot);
        }
    }

}
