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

import com.velocitypowered.api.event.scoreboard.ObjectiveEvent;
import com.velocitypowered.api.event.scoreboard.ScoreboardEventSource;
import com.velocitypowered.api.event.scoreboard.TeamEvent;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VelocityScoreboard implements ProxyScoreboard {

    public static final ProtocolVersion MAXIMUM_SUPPORTED_VERSION = ProtocolVersion.MINECRAFT_1_21;

    @NotNull
    private final ScoreboardEventSource eventSource;

    @NotNull
    private final ConnectedPlayer viewer;

    private final Map<String, VelocityObjective> objectives = new ConcurrentHashMap<>();
    private final Map<String, VelocityTeam> teams = new ConcurrentHashMap<>();
    private final Map<DisplaySlot, VelocityObjective> displaySlots = new ConcurrentHashMap<>();
    private final Map<String, VelocityTeam> teamEntries = new ConcurrentHashMap<>();
    private final DownstreamScoreboard downstream;

    public VelocityScoreboard(@NotNull ScoreboardEventSource eventSource, @NotNull ConnectedPlayer viewer,
                              @NotNull DownstreamScoreboard downstream) {
        this.eventSource = eventSource;
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
        VelocityObjective objective = ((VelocityObjective.Builder)builder).build(this);
        if (objectives.containsKey(objective.getName())) throw new IllegalStateException("An objective with this name (" + objective.getName() + ") already exists in this scoreboard");
        objectives.put(objective.getName(), objective);
        objective.sendRegister();
        eventSource.fireEvent(new ObjectiveEvent.Register(viewer, this, objective));
        if (objective.getDisplaySlot() != null) {
            displaySlots.put(objective.getDisplaySlot(), objective);
            eventSource.fireEvent(new ObjectiveEvent.Display(viewer, this, objective, objective.getDisplaySlot()));
        }
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
        if (teams.containsKey(team.getName())) throw new IllegalStateException("A team with this name (" + team.getName() + ") already exists");
        for (String entry : team.getEntriesRaw()) {
            VelocityTeam entryTeam = getTeamFromEntry(entry);
            if (entryTeam != null) {
                throw new IllegalStateException("An entry with named (" + entry + ") already exists in team " + entryTeam.getName());
            }
            teamEntries.put(entry, team);
        }

        teams.put(team.getName(), team);
        team.sendRegister();
        eventSource.fireEvent(new TeamEvent.Register(viewer, this, team));
        return team;
    }

    @Override
    @Nullable
    public VelocityTeam getTeam(@NotNull String teamName) {
        return teams.get(teamName);
    }

    @Override
    @NotNull
    public Set<ProxyTeam> getTeams() {
        return Set.copyOf(teams.values());
    }

    @ApiStatus.Internal
    @Nullable
    public VelocityTeam getTeamFromEntry(String entry) {
        return teamEntries.get(entry);
    }

    @ApiStatus.Internal
    public void addEntryToTeam(@NotNull String entry, @NotNull VelocityTeam team) {
        teamEntries.put(entry, team);
    }

    @ApiStatus.Internal
    public void removeEntryFromTeam(@NotNull String entry, @NotNull VelocityTeam team) {
        teamEntries.remove(entry, team);
    }

    @Override
    public void unregisterTeam(@NotNull String teamName) {
        if (!teams.containsKey(teamName)) throw new IllegalStateException("This scoreboard does not contain a team named " + teamName);
        VelocityTeam team = teams.remove(teamName);
        team.unregister();
        team.getEntriesRaw().forEach(teamEntries::remove);
    }

    public void setDisplaySlot(@NotNull DisplaySlot displaySlot, @NotNull VelocityObjective objective) {
        VelocityObjective previous = displaySlots.put(displaySlot, objective);
        if (previous != null) previous.clearDisplaySlot();
    }

    public void resend() {
        for (VelocityTeam team : teams.values()) {
            team.sendRegister();
        }
        for (VelocityObjective objective : objectives.values()) {
            objective.sendRegister();
            for (ProxyScore score : objective.getAllScores()) {
                ((VelocityScore)score).sendUpdate();
            }
        }
    }

    @Override
    @Nullable
    public ProxyObjective getObjective(@NotNull DisplaySlot displaySlot) {
        return displaySlots.get(displaySlot);
    }

    /**
     * Returns server.
     * @return  Server
     */
    @NotNull
    public ScoreboardEventSource getEventSource() {
        return eventSource;
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
                }

                // Check if backend wanted to display an objective in this slot
                for (DisplaySlot slot : DisplaySlot.values()) {
                    if (displaySlots.containsKey(slot)) continue; // This slot is occupied by proxy
                    DownstreamObjective obj = downstream.getObjective(slot);
                    if (obj != null) {
                        // This slot is only used by backend, display it (may send unnecessary packets)
                        viewer.getConnection().write(new DisplayObjectivePacket(slot, obj.getName()));
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

    public void sendPacket(@NotNull TeamPacket packet, @NotNull VelocityTeam affectedTeam) {
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
                    viewer.getConnection().write(new TeamPacket(TeamPacket.TeamAction.REGISTER, team.getName(), team.getProperties(), team.getEntriesRaw()));
                }

                // Check if removed players belonged to backend teams
                for (DownstreamTeam dTeam : downstream.getDownstreamTeams()) {
                    Collection<String> teamEntries = dTeam.getEntriesRaw();
                    for (String removedEntry : affectedTeam.getEntriesRaw()) {
                        if (teamEntries.contains(removedEntry)) {
                            // Backend team has this player, add back
                            viewer.getConnection().write(TeamPacket.addOrRemovePlayer(dTeam.getName(), removedEntry, true));
                        }
                    }
                }
            }
            case UPDATE, ADD_PLAYER -> {
                // Nothing should be needed here
                viewer.getConnection().write(packet);
            }
            case REMOVE_PLAYER -> {
                viewer.getConnection().write(packet);

                // Check if backend wanted to display this player
                for (DownstreamTeam team : downstream.getDownstreamTeams()) {
                    Collection<String> teamEntries = team.getEntriesRaw();
                    for (String removedEntry : affectedTeam.getEntriesRaw()) {
                        if (teamEntries.contains(removedEntry)) {
                            // Backend team has this player, add back
                            viewer.getConnection().write(TeamPacket.addOrRemovePlayer(team.getName(), removedEntry, true));
                        }
                    }
                }
            }
        }
    }
}
