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

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.DisplaySlot;
import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.proxy.data.LoggerManager;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a downstream tracker that reflects on what the backend tried to display to the player.
 */
public class DownstreamScoreboard {

    /** Registered objectives on the backend */
    private final Map<String, DownstreamObjective> objectives = new ConcurrentHashMap<>();

    /** Registered teams on the backend */
    private final Map<String, DownstreamTeam> teams = new ConcurrentHashMap<>();

    /** Display slots assigned to objectives */
    private final Map<DisplaySlot, DownstreamObjective> displaySlots = new ConcurrentHashMap<>();

    /** Viewer this scoreboard view belongs to */
    @NotNull
    private final Player viewer;

    /**
     * Constructs new instance with given parameter.
     *
     * @param   viewer
     *          Player who this view will belong to
     */
    public DownstreamScoreboard(@NotNull Player viewer) {
        this.viewer = viewer;
    }

    /**
     * Handles incoming objective packet coming from backend and updates tracked values.
     *
     * @param   packet
     *          Objective packet coming from backend
     * @return  {@code true} if packet is invalid and should be cancelled, {@code false} if not
     */
    public boolean handle(@NotNull ObjectivePacket packet) {
        switch (packet.getAction()) {
            case REGISTER -> {
                if (objectives.containsKey(packet.getObjectiveName())) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard already contains objective called " + packet.getObjectiveName());
                    return true;
                } else {
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
                    return true;
                }
                displaySlots.entrySet().removeIf(entry -> entry.getValue().getName().equals(packet.getObjectiveName()));
            }
            case UPDATE -> {
                DownstreamObjective objective = objectives.get(packet.getObjectiveName());
                if (objective == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain objective called " + packet.getObjectiveName() + ", cannot update");
                    return true;
                } else {
                    objective.update(packet);
                }
            }
        }
        return false;
    }

    /**
     * Handles incoming display objective packet coming from backend and updates tracked values.
     *
     * @param   packet
     *          Display objective packet coming from backend
     * @return  {@code true} if packet is invalid and should be cancelled, {@code false} if not
     */
    public boolean handle(@NotNull DisplayObjectivePacket packet) {
        DownstreamObjective objective = objectives.get(packet.getObjectiveName());
        if (objective == null) {
            LoggerManager.invalidDownstreamPacket("Cannot set display slot of unknown objective " + packet.getObjectiveName());
            return true;
        } else {
            DownstreamObjective previous = displaySlots.put(packet.getPosition(), objective);
            if (previous != null) previous.setDisplaySlot(null);
            objective.setDisplaySlot(packet.getPosition());
            return false;
        }
    }

    /**
     * Handles incoming score packet coming from backend and updates tracked values.
     *
     * @param   packet
     *          Score packet coming from backend
     * @return  {@code true} if packet is invalid and should be cancelled, {@code false} if not
     */
    public boolean handle(@NotNull ScorePacket packet) {
        if (packet.getAction() == ScorePacket.ScoreAction.SET) {
            return handleSet(packet.getObjectiveName(), packet.getScoreHolder(), packet.getValue(), null, null);
        } else {
            return handleReset(packet.getObjectiveName(), packet.getScoreHolder());
        }
    }

    /**
     * Handles incoming set score packet coming from backend and updates tracked values.
     *
     * @param   packet
     *          Set score packet coming from backend
     * @return  {@code true} if packet is invalid and should be cancelled, {@code false} if not
     */
    public boolean handle(@NotNull ScoreSetPacket packet) {
        return handleSet(packet.getObjectiveName(), packet.getScoreHolder(), packet.getValue(),
                packet.getDisplayName() == null ? null : packet.getDisplayName().getComponent(), packet.getNumberFormat());
    }

    /**
     * Handles incoming reset score packet coming from backend and updates tracked values.
     *
     * @param   packet
     *          Reset score packet coming from backend
     * @return  {@code true} if packet is invalid and should be cancelled, {@code false} if not
     */
    public boolean handle(@NotNull ScoreResetPacket packet) {
        return handleReset(packet.getObjectiveName(), packet.getScoreHolder());
    }

    private boolean handleSet(@NotNull String objectiveName, @NotNull String holder, int value,
                              @Nullable Component displayName, @Nullable NumberFormat numberFormat) {
        DownstreamObjective objective = objectives.get(objectiveName);
        if (objective == null) {
            LoggerManager.invalidDownstreamPacket("Cannot set score for unknown objective " + objectiveName);
            return true;
        } else {
            objective.setScore(holder, value, displayName, numberFormat);
            return false;
        }
    }

    private boolean handleReset(@Nullable String objectiveName, @NotNull String holder) {
        if (objectiveName == null) {
            for (DownstreamObjective objective : objectives.values()) {
                objective.removeScore(holder);
            }
        } else {
            DownstreamObjective objective = objectives.get(objectiveName);
            if (objective == null) {
                LoggerManager.invalidDownstreamPacket("Cannot reset score for unknown objective " + objectiveName);
                return true;
            } else {
                objective.removeScore(holder);
            }
        }
        return false;
    }

    /**
     * Handles incoming team packet coming from backend and updates tracked values.
     *
     * @param   packet
     *          Team packet coming from backend
     * @return  {@code true} if packet is invalid and should be cancelled, {@code false} if not
     */
    public boolean handle(@NotNull TeamPacket packet) {
        switch (packet.getAction()) {
            case REGISTER -> {
                if (teams.containsKey(packet.getName())) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard already contains team called " + packet.getName());
                    return true;
                } else {
                    teams.put(packet.getName(), new DownstreamTeam(packet.getName(), packet.getProperties(), packet.getEntries()));
                }
            }
            case UNREGISTER -> {
                if (teams.remove(packet.getName()) == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain team called " + packet.getName() + ", cannot unregister");
                    return true;
                }
            }
            case UPDATE -> {
                DownstreamTeam team = teams.get(packet.getName());
                if (team == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain team called " + packet.getName() + ", cannot update");
                    return true;
                } else {
                    team.setProperties(packet.getProperties());
                }
            }
            case ADD_PLAYER -> {
                DownstreamTeam team = teams.get(packet.getName());
                if (team == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain team called " + packet.getName() + ", cannot add entries");
                    return true;
                } else {
                    team.addEntries(packet.getEntries());
                }
                for (DownstreamTeam allTeams : teams.values()) {
                    if (allTeams == team) continue; // Current team, do not remove from that one
                    allTeams.removeEntriesIfPresent(packet.getEntries());
                }
            }
            case REMOVE_PLAYER -> {
                DownstreamTeam team = teams.get(packet.getName());
                if (team == null) {
                    LoggerManager.invalidDownstreamPacket("This scoreboard does not contain team called " + packet.getName() + ", cannot remove entries");
                    return true;
                } else {
                    team.removeEntries(packet.getEntries());
                }
            }
        }
        return false;
    }

    @Nullable
    public DownstreamObjective getObjective(@NotNull DisplaySlot displaySlot) {
        return displaySlots.get(displaySlot);
    }

    @Nullable
    public DownstreamObjective getObjective(@NotNull String objectiveName) {
        return objectives.get(objectiveName);
    }

    @Nullable
    public DownstreamTeam getTeam(@NotNull String teamName) {
        return teams.get(teamName);
    }

    @NotNull
    public Collection<DownstreamTeam> getAllTeams() {
        return teams.values();
    }

    /**
     * Clears this scoreboard on server switch when JoinGame packet is received.
     */
    public void clear() {
        objectives.clear();
        teams.clear();
    }
}
