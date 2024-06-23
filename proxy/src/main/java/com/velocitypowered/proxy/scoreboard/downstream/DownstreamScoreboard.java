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

import com.google.common.base.Preconditions;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.DisplaySlot;
import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.proxy.data.LoggerManager;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownstreamScoreboard {

    private final Map<String, DownstreamObjective> objectives = new ConcurrentHashMap<>();
    private final Map<String, DownstreamTeam> teams = new ConcurrentHashMap<>();
    private final Map<DisplaySlot, DownstreamObjective> displaySlots = new ConcurrentHashMap<>();
    @NotNull private final Player viewer;

    public DownstreamScoreboard(@NotNull Player viewer) {
        this.viewer = viewer;
    }

    public void handle(@NotNull ObjectivePacket packet) {
        switch (packet.getAction()) {
            case REGISTER -> {
                if (objectives.containsKey(packet.getObjectiveName())) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard already contains objective called " + packet.getObjectiveName());
                } else {
                    Preconditions.checkNotNull(packet.getTitle(), "Objective title must be present for register action");
                    objectives.put(packet.getObjectiveName(), new DownstreamObjective(
                            packet.getObjectiveName(),
                            packet.getTitle(),
                            packet.getHealthDisplay(),
                            packet.getNumberFormat()
                    ));
                }
            }
            case UNREGISTER -> {
                if (objectives.remove(packet.getObjectiveName()) == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain objective called " + packet.getObjectiveName() + ", cannot unregister");
                }
                displaySlots.entrySet().removeIf(entry -> entry.getValue().getName().equals(packet.getObjectiveName()));
            }
            case UPDATE -> {
                DownstreamObjective objective = objectives.get(packet.getObjectiveName());
                if (objective == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain objective called " + packet.getObjectiveName() + ", cannot update");
                } else {
                    objective.update(packet);
                }
            }
        }
    }

    public void handle(@NotNull DisplayObjectivePacket packet) {
        DownstreamObjective objective = objectives.get(packet.getObjectiveName());
        if (objective == null) {
            LoggerManager.invalidDownstreamPacket("Cannot set display slot of unknown objective " + packet.getObjectiveName());
        } else {
            DownstreamObjective previous = displaySlots.put(packet.getPosition(), objective);
            if (previous != null) previous.setDisplaySlot(null);
            objective.setDisplaySlot(packet.getPosition());
        }
    }

    public void handle(@NotNull ScorePacket packet) {
        if (packet.getAction() == ScorePacket.ScoreAction.SET) {
            Preconditions.checkNotNull(packet.getObjectiveName(), "Score value must be present for set action");
            handleSet(packet.getObjectiveName(), packet.getScoreHolder(), packet.getValue(), null, null);
        } else {
            handleReset(packet.getObjectiveName(), packet.getScoreHolder());
        }
    }

    public void handle(@NotNull ScoreSetPacket packet) {
        handleSet(packet.getObjectiveName(), packet.getScoreHolder(), packet.getValue(), packet.getDisplayName(), packet.getNumberFormat());
    }

    public void handle(@NotNull ScoreResetPacket packet) {
        handleReset(packet.getObjectiveName(), packet.getScoreHolder());
    }

    private void handleSet(@NotNull String objectiveName, @NotNull String holder, int value,
                           @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        DownstreamObjective objective = objectives.get(objectiveName);
        if (objective == null) {
            LoggerManager.invalidDownstreamPacket("Cannot set score for unknown objective " + objectiveName);
        } else {
            objective.setScore(holder, value, displayName, numberFormat);
        }
    }

    private void handleReset(@Nullable String objectiveName, @NotNull String holder) {
        if (objectiveName == null) {
            for (DownstreamObjective objective : objectives.values()) {
                objective.removeScore(holder);
            }
        } else {
            DownstreamObjective objective = objectives.get(objectiveName);
            if (objective == null) {
                LoggerManager.invalidDownstreamPacket("Cannot reset score for unknown objective " + objectiveName);
            } else {
                objective.removeScore(holder);
            }
        }
    }

    public void handle(@NotNull TeamPacket packet) {
        switch (packet.getAction()) {
            case REGISTER -> {
                Preconditions.checkNotNull(packet.getProperties(), "Team properties must be present for register action");
                Preconditions.checkNotNull(packet.getEntries(), "Team entries must be present for register action");
                if (teams.containsKey(packet.getName())) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard already contains team called " + packet.getName());
                } else {
                    teams.put(packet.getName(), new DownstreamTeam(packet.getName(), packet.getProperties(), packet.getEntries()));
                }
            }
            case UNREGISTER -> {
                if (teams.remove(packet.getName()) == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain team called " + packet.getName() + ", cannot unregister");
                }
            }
            case UPDATE -> {
                Preconditions.checkNotNull(packet.getProperties(), "Team properties must be present for update action");
                DownstreamTeam team = teams.get(packet.getName());
                if (team == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain team called " + packet.getName() + ", cannot update");
                } else {
                    team.setProperties(packet.getProperties());
                }
            }
            case ADD_PLAYER -> {
                Preconditions.checkNotNull(packet.getEntries(), "Team entries must be present for add player action");
                DownstreamTeam team = teams.get(packet.getName());
                if (team == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain team called " + packet.getName() + ", cannot add entries");
                } else {
                    team.addEntries(packet.getEntries());
                }
                for (DownstreamTeam allTeams : teams.values()) {
                    if (allTeams == team) continue; // Current team, do not remove from that one
                    allTeams.removeEntriesIfPresent(packet.getEntries());
                }
            }
            case REMOVE_PLAYER -> {
                Preconditions.checkNotNull(packet.getEntries(), "Team entries must be present for remove player action");
                DownstreamTeam team = teams.get(packet.getName());
                if (team == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain team called " + packet.getName() + ", cannot remove entries");
                } else {
                    team.removeEntries(packet.getEntries());
                }
            }
        }
    }

    /**
     * Clears this scoreboard on server switch when JoinGame packet is received.
     */
    public void clear() {
        objectives.clear();
        teams.clear();
    }
}
