package com.velocitypowered.proxy.protocol.packet.downstream;
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

import com.velocitypowered.api.scoreboard.DisplaySlot;
import com.velocitypowered.api.scoreboard.HealthDisplay;
import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.scoreboard.ObjectivePacket;
import com.velocitypowered.proxy.scoreboard.ScorePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownstreamObjective {

    @NotNull private final String objectiveName;
    @Nullable private String titleLegacy;
    @Nullable private ComponentHolder titleModern;
    @Nullable private HealthDisplay healthDisplay;
    @Nullable private NumberFormat numberFormat;
    @Nullable private DisplaySlot displaySlot;
    @NotNull private final Map<String, DownstreamScore> scores = new ConcurrentHashMap<>();

    public DownstreamObjective(@NotNull String objectiveName, @Nullable String titleLegacy,
                               @Nullable ComponentHolder titleModern, @Nullable HealthDisplay healthDisplay,
                               @Nullable NumberFormat numberFormat, @Nullable DisplaySlot displaySlot) {
        this.objectiveName = objectiveName;
        this.titleLegacy = titleLegacy;
        this.titleModern = titleModern;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
        this.displaySlot = displaySlot;
    }

    public void setDisplaySlot(@NotNull DisplaySlot displaySlot) {
        this.displaySlot = displaySlot;
    }

    public void update(@NotNull ObjectivePacket packet) {
        titleLegacy = packet.getTitleLegacy();
        titleModern = packet.getTitleModern();
        healthDisplay = packet.getHealthDisplay();
        numberFormat = packet.readNumberFormat();
    }

    public void setScore(@NotNull String holder, int value, @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        if (scores.containsKey(holder)) {
            scores.get(holder).update(value, displayName, numberFormat);
        } else {
            scores.put(holder, new DownstreamScore(holder, value, displayName, numberFormat));
        }
    }

    public void removeScore(@NotNull String holder) {
        scores.remove(holder);
    }
}