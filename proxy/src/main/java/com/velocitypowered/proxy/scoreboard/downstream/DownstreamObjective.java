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

package com.velocitypowered.proxy.scoreboard.downstream;

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.scoreboard.*;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An objective coming from a downstream scoreboard.
 */
public class DownstreamObjective implements Objective {

    /** Name of the objective */
    @NotNull
    private final String objectiveName;

    /** Objective title */
    @NotNull
    private TextHolder title;

    /** Health display (1.8+) */
    @NotNull
    private HealthDisplay healthDisplay;

    /** Default number format for all scores (1.20.3+) */
    @Nullable
    private NumberFormat numberFormat;

    /** Display slot of the objective */
    @Nullable
    private DisplaySlot displaySlot;

    /** Registered scores */
    @NotNull
    private final Map<String, DownstreamScore> scores = new ConcurrentHashMap<>();

    /**
     * Constructs new instance with given parameters.
     *
     * @param   objectiveName
     *          Objective name
     * @param   title
     *          Objective title
     * @param   healthDisplay
     *          Health display type (1.8+)
     * @param   numberFormat
     *          Default number format for all scores (1.20.3+)
     */
    public DownstreamObjective(@NotNull String objectiveName, @NotNull TextHolder title,
                               @NotNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat) {
        this.objectiveName = objectiveName;
        this.title = title;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
    }

    @Override
    @NotNull
    public String getName() {
        return objectiveName;
    }

    @Override
    @NotNull
    public TextHolder getTitle() {
        return title;
    }

    @Override
    @NotNull
    public HealthDisplay getHealthDisplay() {
        return healthDisplay;
    }

    @Override
    @Nullable
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    @Override
    public @Nullable Score getScore(@NotNull String holder) {
        return scores.get(holder);
    }

    @Override
    @Nullable
    public DisplaySlot getDisplaySlot() {
        return displaySlot;
    }

    @Override
    @NotNull
    public Collection<DownstreamScore> getAllScores() {
        return Collections.unmodifiableCollection(scores.values());
    }

    /**
     * Sets display slot of this objective.
     *
     * @param   displaySlot
     *          New display slot
     */
    public void setDisplaySlot(@Nullable DisplaySlot displaySlot) {
        this.displaySlot = displaySlot;
    }

    /**
     * Updates objective properties.
     *
     * @param   packet
     *          Packet to take parameters from
     */
    public void update(@NotNull ObjectivePacket packet) {
        title = packet.getTitle();
        healthDisplay = packet.getHealthDisplay();
        numberFormat = packet.getNumberFormat();
    }

    /**
     * Sets score into the objective.
     *
     * @param   holder
     *          Score holder
     * @param   value
     *          Score value
     * @param   displayName
     *          Score holder's display name (1.20.3+)
     * @param   numberFormat
     *          Number formatter for score (1.20.3+)
     */
    public void setScore(@NotNull String holder, int value, @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        scores.computeIfAbsent(holder, DownstreamScore::new).update(value, displayName, numberFormat);
    }

    /**
     * Removes score from this objective.
     *
     * @param   holder
     *          Score holder to remove
     */
    public void removeScore(@NotNull String holder) {
        scores.remove(holder);
    }

    /**
     * Creates a dump of this objective into a list of lines.
     *
     * @return  dump of this objective
     */
    @NotNull
    public List<String> dump() {
        List<String> content = new ArrayList<>();
        content.add("  " + objectiveName + ":");
        content.add("    Title: " + title);
        content.add("    HealthDisplay: " + healthDisplay);
        content.add("    NumberFormat: " + numberFormat);
        content.add("    DisplaySlot: " + displaySlot);
        content.add("    Scores (" + scores.size() + "):");
        for (DownstreamScore score : scores.values()) {
            content.addAll(score.dump());
        }
        return content;
    }
}
