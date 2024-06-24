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
import com.velocitypowered.api.scoreboard.*;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket.ObjectiveAction;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamObjective;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScore;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VelocityScoreboard implements ProxyScoreboard {

    public static final ProtocolVersion MAXIMUM_SUPPORTED_VERSION = ProtocolVersion.MINECRAFT_1_21;

    @NotNull
    private final ConnectedPlayer viewer;
    private final Map<String, VelocityObjective> objectives = new ConcurrentHashMap<>();
    private final Map<String, VelocityTeam> teams = new ConcurrentHashMap<>();
    private final Map<DisplaySlot, VelocityObjective> displaySlots = new ConcurrentHashMap<>();
    private final DownstreamScoreboard downstream;

    public VelocityScoreboard(@NotNull ConnectedPlayer viewer, @NotNull DownstreamScoreboard downstream) {
        this.viewer = viewer;
        this.downstream = downstream;
    }

    @NotNull
    public ConnectedPlayer getViewer() {
        return viewer;
    }

    @Override
    @NotNull
    public VelocityTeam.Builder teamBuilder(@NotNull String name) {
        return new VelocityTeam.Builder(name);
    }

    @Override
    @NotNull
    public VelocityTeam.PropertyBuilder teamPropertyBuilder() {
        return new VelocityTeam.PropertyBuilder();
    }

    @Override
    @NotNull
    public VelocityObjective.Builder objectiveBuilder(@NotNull String name) {
        return new VelocityObjective.Builder(name);
    }

    @Override
    @NotNull
    public VelocityObjective registerObjective(@NotNull ProxyObjective.Builder builder) {
        final VelocityObjective objective = ((VelocityObjective.Builder)builder).build(this);
        if (objectives.containsKey(objective.getName())) throw new IllegalStateException("Objective with this name already exists");
        objectives.put(objective.getName(), objective);
        if (objective.getDisplaySlot() != null) {
            displaySlots.put(objective.getDisplaySlot(), objective);
        }
        objective.sendRegister();
        return objective;
    }

    @Override
    @Nullable
    public VelocityObjective getObjective(@NotNull String name) {
        return objectives.get(name);
    }

    @Override
    public @NotNull Set<ProxyObjective> getObjectives() {
        return Set.copyOf(objectives.values());
    }

    @Override
    public void unregisterObjective(@NotNull String objectiveName) throws IllegalStateException {
        if (!objectives.containsKey(objectiveName)) throw new IllegalStateException("This scoreboard does not contain an objective named " + objectiveName);
        displaySlots.entrySet().removeIf(entry -> entry.getValue().getName().equals(objectiveName));
        objectives.remove(objectiveName).unregister();
    }

    @NotNull
    @Override
    public VelocityTeam registerTeam(@NotNull ProxyTeam.Builder builder) {
        VelocityTeam team = ((VelocityTeam.Builder)builder).build(this);
        if (teams.containsKey(team.getName())) throw new IllegalStateException("Team with this name already exists");
        teams.put(team.getName(), team);
        team.sendRegister();
        return team;
    }

    @Override
    @Nullable
    public VelocityTeam getTeam(@NotNull String teamName) {
        return teams.get(teamName);
    }

    @Override
    public @NotNull Set<ProxyTeam> getTeams() {
        return Set.copyOf(teams.values());
    }

    @Override
    public void unregisterTeam(@NotNull String teamName) {
        if (!teams.containsKey(teamName)) throw new IllegalStateException("This scoreboard does not contain a team named " + teamName);
        teams.remove(teamName).unregister();
    }

    public void setDisplaySlot(@NotNull DisplaySlot displaySlot, @NotNull VelocityObjective objective) {
        VelocityObjective previous = displaySlots.put(displaySlot, objective);
        if (previous != null) previous.clearDisplaySlot();
    }

    public Set<VelocityTeam> getAllTeams() {
        return Set.copyOf(teams.values());
    }

    public void resend() {
        for (VelocityTeam team : teams.values()) {
            team.sendRegister();
        }
        for (VelocityObjective objective : objectives.values()) {
            objective.sendRegister();
            for (VelocityScore score : objective.getScores()) {
                score.sendUpdate();
            }
        }
    }

    @Override
    @Nullable
    public ProxyObjective getObjective(@NotNull DisplaySlot displaySlot) {
        return displaySlots.get(displaySlot);
    }

    public void sendPacket(@NotNull DisplayObjectivePacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        viewer.getConnection().write(packet);

        // Check if a slot was freed
        for (DisplaySlot slot : DisplaySlot.values()) {
            if (!displaySlots.containsKey(slot)) {
                // Slot is free, check if backend wants to display something
                DownstreamObjective objective = downstream.getObjective(packet.getPosition());
                if (objective != null) {
                    // Backend tried to display something in this slot, allow it now
                    viewer.getConnection().write(new DisplayObjectivePacket(slot, objective.getName()));
                }
            }
        }
    }

    public void sendPacket(@NotNull ObjectivePacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        switch (packet.getAction()) {
            case REGISTER -> {
                DownstreamObjective objective = downstream.getObjective(packet.getObjectiveName());
                if (objective != null) {
                    // Backend is using this scoreboard, unregister it to allow this
                    viewer.getConnection().write(new ObjectivePacket(ObjectiveAction.UNREGISTER, packet.getObjectiveName(), null, null, null));
                }
                viewer.getConnection().write(packet);
            }
            case UNREGISTER -> {
                viewer.getConnection().write(packet);
                // Check if backend wanted to display an objective with this name
                DownstreamObjective objective = downstream.getObjective(packet.getObjectiveName());
                if (objective != null) {
                    // Backend wants this too, send the objective and scores
                    viewer.getConnection().write(new ObjectivePacket(ObjectiveAction.REGISTER, objective.getName(), objective.getTitle(), objective.getHealthDisplay(), objective.getNumberFormat()));
                    for (DownstreamScore score : objective.getAllScores()) {
                        if (viewer.getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                            ComponentHolder cHolder = score.getDisplayName() == null ? null : new ComponentHolder(viewer.getProtocolVersion(), score.getDisplayName());
                            viewer.getConnection().write(new ScoreSetPacket(score.getHolder(), objective.getName(), score.getScore(), cHolder, score.getNumberFormat()));
                        } else {
                            viewer.getConnection().write(new ScorePacket(ScorePacket.ScoreAction.SET, score.getHolder(), objective.getName(), score.getScore()));
                        }
                    }
                    // Send display slot if free
                    if (objective.getDisplaySlot() != null && !displaySlots.containsKey(objective.getDisplaySlot())) {
                        viewer.getConnection().write(new DisplayObjectivePacket(objective.getDisplaySlot(), objective.getName()));
                    }
                }
            }
            case UPDATE -> {
                // Nothing should be needed here
                viewer.getConnection().write(packet);
            }
        }
    }

    public void sendPacket(@NotNull ScorePacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        viewer.getConnection().write(packet);
    }

    public void sendPacket(@NotNull ScoreSetPacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        viewer.getConnection().write(packet);
    }

    public void sendPacket(@NotNull ScoreResetPacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        viewer.getConnection().write(packet);
    }

    public void sendPacket(@NotNull TeamPacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        switch (packet.getAction()) {
            case REGISTER -> {
                DownstreamTeam team = downstream.getTeam(packet.getName());
                if (team != null) {
                    // Backend is using this team, unregister it to allow this
                    viewer.getConnection().write(TeamPacket.unregister(packet.getName()));
                }
                viewer.getConnection().write(packet);
            }
            case UNREGISTER -> {
                viewer.getConnection().write(packet);
                // Check if backend wanted to display a team with this name
                DownstreamTeam team = downstream.getTeam(packet.getName());
                if (team != null) {
                    // Backend wants this too, send it
                    viewer.getConnection().write(new TeamPacket(TeamPacket.TeamAction.REGISTER, team.getName(), team.getProperties(), team.getEntries()));
                }
            }
            case UPDATE, ADD_PLAYER -> {
                // Nothing should be needed here
                viewer.getConnection().write(packet);
            }
            case REMOVE_PLAYER -> {
                viewer.getConnection().write(packet);
                // Check if backend wanted to display this player
                for (DownstreamTeam team : downstream.getAllTeams()) {
                    for (String removedEntry : packet.getEntries()) {
                        if (team.getEntries().contains(removedEntry)) {
                            // Backend team has this player, add back
                            viewer.getConnection().write(TeamPacket.addOrRemovePlayer(team.getName(), removedEntry, true));
                        }
                    }
                }
            }
        }
    }
}
