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
import com.velocitypowered.proxy.protocol.MinecraftPacket;
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
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

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
    private final Queue<MinecraftPacket> packetQueue = new ConcurrentLinkedDeque<>();

    /** Flag tracking if this scoreboard is frozen. While frozen, no packets will get through. */
    private boolean frozen;

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
    public @NotNull Collection<ProxyObjective> getObjectives() {
        return Collections.unmodifiableCollection(objectives.values());
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
            VelocityTeam oldTeam = teamEntries.put(entry, team);
            if (oldTeam != null) {
                oldTeam.removeEntrySilent(entry);
            }
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
    public Collection<ProxyTeam> getTeams() {
        return Collections.unmodifiableCollection(teams.values());
    }

    @NotNull
    public Collection<VelocityTeam> getTeamsRaw() {
        return teams.values();
    }

    @ApiStatus.Internal
    @Nullable
    public VelocityTeam addEntryToTeam(@NotNull String entry, @NotNull VelocityTeam team) {
        return teamEntries.put(entry, team);
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

    /**
     * Resends the entire scoreboard. This function is called on server switch. It mostly performs a raw
     * packet write instead of calling existing register functions to skip checks and avoid
     * potentially incorrect behavior, such as when a team/objective name is on both proxy and backend.
     */
    public synchronized void resend() {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        for (VelocityTeam team : teams.values()) {
            viewer.getConnection().write(new TeamPacket(
                    TeamPacket.TeamAction.REGISTER,
                    team.getName(),
                    team.getProperties(),
                    team.getEntriesRaw().toArray(String[]::new)
            ));
        }
        for (VelocityObjective objective : objectives.values()) {
            viewer.getConnection().write(new ObjectivePacket(
                    ObjectiveAction.REGISTER,
                    objective.getName(),
                    objective.getTitle(),
                    objective.getHealthDisplay(),
                    objective.getNumberFormat()
            ));
            if (objective.getDisplaySlot() != null) {
                viewer.getConnection().write(new DisplayObjectivePacket(
                        objective.getDisplaySlot(),
                        objective.getName()
                ));
            }
            for (ProxyScore score : objective.getAllScores()) {
                if (viewer.getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                    ComponentHolder cHolder = score.getDisplayName() == null ? null : new ComponentHolder(objective.getScoreboard().getViewer().getProtocolVersion(), score.getDisplayName());
                    viewer.getConnection().write(new ScoreSetPacket(score.getHolder(), objective.getName(), score.getScore(), cHolder, score.getNumberFormat()));
                } else {
                    viewer.getConnection().write(new ScorePacket(ScorePacket.ScoreAction.SET, score.getHolder(), objective.getName(), score.getScore()));
                }
            }
        }
        processQueue();
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

    public synchronized void sendPacket(@NotNull DisplayObjectivePacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        queuePacket(packet);

        // Check if a slot was freed
        for (DisplaySlot slot : DisplaySlot.values()) {
            if (!displaySlots.containsKey(slot)) {
                // Slot is free, check if backend wants to display something
                DownstreamObjective objective = downstream.getObjective(packet.getPosition());
                if (objective != null) {
                    // Backend tried to display something in this slot, allow it now
                    queuePacket(new DisplayObjectivePacket(slot, objective.getName()));
                }
            }
        }
    }

    public synchronized void sendPacket(@NotNull ObjectivePacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        switch (packet.getAction()) {
            case REGISTER -> {
                DownstreamObjective objective = downstream.getObjective(packet.getObjectiveName());
                if (objective != null) {
                    // Backend is using this scoreboard, unregister it to allow this
                    queuePacket(new ObjectivePacket(ObjectiveAction.UNREGISTER, packet.getObjectiveName(), null, null, null));
                }
                queuePacket(packet);
            }
            case UNREGISTER -> {
                queuePacket(packet);

                // Check if backend wanted to display an objective with this name
                DownstreamObjective objective = downstream.getObjective(packet.getObjectiveName());
                if (objective != null) {
                    // Backend wants this too, send the objective and scores
                    queuePacket(new ObjectivePacket(ObjectiveAction.REGISTER, objective.getName(), objective.getTitle(), objective.getHealthDisplay(), objective.getNumberFormat()));
                    for (DownstreamScore score : objective.getAllScores()) {
                        if (viewer.getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                            queuePacket(new ScoreSetPacket(score.getHolder(), objective.getName(), score.getScore(), score.getDisplayNameHolder(), score.getNumberFormat()));
                        } else {
                            queuePacket(new ScorePacket(ScorePacket.ScoreAction.SET, score.getHolder(), objective.getName(), score.getScore()));
                        }
                    }
                }

                // Check if backend wanted to display an objective in this slot
                for (DisplaySlot slot : DisplaySlot.values()) {
                    if (displaySlots.containsKey(slot)) continue; // This slot is occupied by proxy
                    DownstreamObjective obj = downstream.getObjective(slot);
                    if (obj != null) {
                        // This slot is only used by backend, display it (may send unnecessary packets)
                        queuePacket(new DisplayObjectivePacket(slot, obj.getName()));
                    }
                }
            }
            case UPDATE -> {
                // Nothing should be needed here
                queuePacket(packet);
            }
        }
    }

    public synchronized void sendPacket(@NotNull ScorePacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        queuePacket(packet);
    }

    public synchronized void sendPacket(@NotNull ScoreSetPacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        queuePacket(packet);
    }

    public synchronized void sendPacket(@NotNull ScoreResetPacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        queuePacket(packet);
    }

    public synchronized void sendPacket(@NotNull TeamPacket packet, @NotNull VelocityTeam affectedTeam) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        switch (packet.getAction()) {
            case REGISTER -> {
                DownstreamTeam team = downstream.getTeam(packet.getName());
                if (team != null) {
                    // Backend is using this team, unregister it to allow this
                    queuePacket(TeamPacket.unregister(packet.getName()));
                }
                queuePacket(packet);
            }
            case UNREGISTER -> {
                queuePacket(packet);
                // Check if backend wanted to display a team with this name
                DownstreamTeam team = downstream.getTeam(packet.getName());
                if (team != null) {
                    // Backend wants this too, send it
                    queuePacket(new TeamPacket(TeamPacket.TeamAction.REGISTER, team.getName(), team.getProperties(), team.getEntries().toArray(String[]::new)));
                }

                // Check if removed players belonged to backend teams
                for (DownstreamTeam dTeam : downstream.getDownstreamTeams()) {
                    Collection<String> teamEntries = dTeam.getEntries();
                    for (String removedEntry : affectedTeam.getEntriesRaw()) {
                        if (teamEntries.contains(removedEntry)) {
                            // Backend team has this player, add back
                            queuePacket(TeamPacket.addOrRemovePlayer(dTeam.getName(), removedEntry, true));
                        }
                    }
                }
            }
            case UPDATE, ADD_PLAYER -> {
                // Nothing should be needed here
                queuePacket(packet);
            }
            case REMOVE_PLAYER -> {
                queuePacket(packet);

                // Check if backend wanted to display this player
                for (DownstreamTeam team : downstream.getDownstreamTeams()) {
                    Collection<String> teamEntries = team.getEntries();
                    for (String removedEntry : affectedTeam.getEntriesRaw()) {
                        if (teamEntries.contains(removedEntry)) {
                            // Backend team has this player, add back
                            queuePacket(TeamPacket.addOrRemovePlayer(team.getName(), removedEntry, true));
                        }
                    }
                }
            }
        }
    }

    /**
     * Marks the scoreboard for freeze. While frozen, no packets will be sent.
     */
    public void freeze() {
        frozen = true;
    }

    private void queuePacket(@NotNull MinecraftPacket packet) {
        if (frozen) {
            packetQueue.add(packet);
            return;
        }
        viewer.getConnection().write(packet);
    }

    private void processQueue() {
        while (!packetQueue.isEmpty()) {
            viewer.getConnection().write(packetQueue.poll());
        }
        frozen = false;
    }
}
