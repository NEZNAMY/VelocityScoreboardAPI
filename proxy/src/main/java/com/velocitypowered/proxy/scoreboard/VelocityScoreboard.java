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
import com.velocitypowered.proxy.data.StringCollection;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket.ObjectiveAction;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamObjective;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScore;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamTeam;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class VelocityScoreboard implements ProxyScoreboard {

    public static final ProtocolVersion MAXIMUM_SUPPORTED_VERSION = ProtocolVersion.MINECRAFT_1_21_9;

    @NonNull
    @Getter
    private final ScoreboardEventSource eventSource;

    @NonNull
    @Getter
    private final ConnectedPlayer viewer;

    private final Map<String, VelocityObjective> objectives = new ConcurrentHashMap<>();
    private final Map<String, VelocityTeam> teams = new ConcurrentHashMap<>();
    private final Map<DisplaySlot, VelocityObjective> displaySlots = new ConcurrentHashMap<>();
    private final Map<String, VelocityTeam> teamEntries = new ConcurrentHashMap<>();
    private final DownstreamScoreboard downstream;

    /** Flag tracking if this scoreboard is frozen. While frozen, no packets will get through. */
    private boolean frozen;

    @Override
    @NotNull
    public VelocityTeam.Builder teamBuilder(@NonNull String name) {
        return new VelocityTeam.Builder(name);
    }

    @Override
    @NotNull
    public VelocityTeam.PropertyBuilder teamPropertyBuilder() {
        return new VelocityTeam.PropertyBuilder();
    }

    @Override
    @NotNull
    public VelocityObjective.Builder objectiveBuilder(@NonNull String name) {
        return new VelocityObjective.Builder(name);
    }

    @Override
    @NotNull
    public VelocityObjective registerObjective(@NonNull ProxyObjective.Builder builder) {
        VelocityObjective objective = ((VelocityObjective.Builder)builder).build(this);
        if (objectives.putIfAbsent(objective.getName(), objective) != null) {
            throw new IllegalStateException("An objective with this name (" + objective.getName() + ") already exists in this scoreboard");
        }
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
    public VelocityObjective getObjective(@NonNull String name) {
        return objectives.get(name);
    }

    @Override
    public @NotNull Collection<ProxyObjective> getObjectives() {
        return Collections.unmodifiableCollection(objectives.values());
    }

    @Override
    public void unregisterObjective(@NonNull String objectiveName) throws IllegalStateException {
        VelocityObjective objective = objectives.remove(objectiveName);
        if (objective == null) throw new IllegalStateException("This scoreboard does not contain an objective named " + objectiveName);
        displaySlots.entrySet().removeIf(entry -> entry.getValue().getName().equals(objectiveName));
        objective.unregister();
    }

    @NotNull
    @Override
    public VelocityTeam registerTeam(@NonNull ProxyTeam.Builder builder) {
        VelocityTeam team = ((VelocityTeam.Builder)builder).build(this);
        if (teams.putIfAbsent(team.getName(), team) != null) {
            throw new IllegalStateException("A team with this name (" + team.getName() + ") already exists");
        }
        if (team.getEntryCollection().getEntry() != null) {
            VelocityTeam oldTeam = teamEntries.put(team.getEntryCollection().getEntry(), team);
            if (oldTeam != null) {
                oldTeam.removeEntrySilent(team.getEntryCollection().getEntry());
            }
        } else {
            for (String entry : team.getEntryCollection().getEntries()) {
                VelocityTeam oldTeam = teamEntries.put(entry, team);
                if (oldTeam != null) {
                    oldTeam.removeEntrySilent(entry);
                }
            }
        }

        team.sendRegister();
        eventSource.fireEvent(new TeamEvent.Register(viewer, this, team));
        return team;
    }

    @Override
    @Nullable
    public VelocityTeam getTeam(@NonNull String teamName) {
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
    public VelocityTeam addEntryToTeam(@NonNull String entry, @NonNull VelocityTeam team) {
        return teamEntries.put(entry, team);
    }

    @ApiStatus.Internal
    public void removeEntryFromTeam(@NonNull String entry, @NonNull VelocityTeam team) {
        teamEntries.remove(entry, team);
    }

    @Override
    public void unregisterTeam(@NonNull String teamName) {
        VelocityTeam team = teams.remove(teamName);
        if (team == null) throw new IllegalStateException("This scoreboard does not contain a team named " + teamName);
        team.unregister();
        if (team.getEntryCollection().getEntry() != null) {
            teamEntries.remove(team.getEntryCollection().getEntry());
        } else {
            for (String entry : team.getEntryCollection().getEntries()) {
                teamEntries.remove(entry);
            }
        }
    }

    public void setDisplaySlot(@NonNull DisplaySlot displaySlot, @NonNull VelocityObjective objective) {
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
                    team.getEntryCollection()
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
        frozen = false;
    }

    @Override
    @Nullable
    public ProxyObjective getObjective(@NonNull DisplaySlot displaySlot) {
        return displaySlots.get(displaySlot);
    }

    public synchronized void sendPacket(@NonNull DisplayObjectivePacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        sendPacketSafe(packet);

        // Check if a slot was freed
        for (DisplaySlot slot : DisplaySlot.values()) {
            if (!displaySlots.containsKey(slot)) {
                // Slot is free, check if backend wants to display something
                DownstreamObjective objective = downstream.getObjective(slot);
                if (objective != null) {
                    // Backend tried to display something in this slot, allow it now
                    sendPacketSafe(new DisplayObjectivePacket(slot, objective.getName()));
                }
            }
        }
    }

    public synchronized void sendPacket(@NonNull ObjectivePacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        switch (packet.getAction()) {
            case REGISTER -> {
                DownstreamObjective objective = downstream.getObjective(packet.getObjectiveName());
                if (objective != null) {
                    // Backend is using this scoreboard, unregister it to allow this
                    sendPacketSafe(new ObjectivePacket(ObjectiveAction.UNREGISTER, packet.getObjectiveName(), null, null, null));
                }
                sendPacketSafe(packet);
            }
            case UNREGISTER -> {
                sendPacketSafe(packet);

                // Check if backend wanted to display an objective with this name
                DownstreamObjective objective = downstream.getObjective(packet.getObjectiveName());
                if (objective != null) {
                    // Backend wants this too, send the objective and scores
                    sendPacketSafe(new ObjectivePacket(ObjectiveAction.REGISTER, objective.getName(), objective.getTitle(), objective.getHealthDisplay(), objective.getNumberFormat()));
                    for (DownstreamScore score : objective.getAllScores()) {
                        if (viewer.getProtocolVersion().noLessThan(ProtocolVersion.MINECRAFT_1_20_3)) {
                            sendPacketSafe(new ScoreSetPacket(score.getHolder(), objective.getName(), score.getScore(), score.getDisplayNameHolder(), score.getNumberFormat()));
                        } else {
                            sendPacketSafe(new ScorePacket(ScorePacket.ScoreAction.SET, score.getHolder(), objective.getName(), score.getScore()));
                        }
                    }
                }

                // Check if backend wanted to display an objective in this slot
                for (DisplaySlot slot : DisplaySlot.values()) {
                    if (displaySlots.containsKey(slot)) continue; // This slot is occupied by proxy
                    DownstreamObjective obj = downstream.getObjective(slot);
                    if (obj != null) {
                        // This slot is only used by backend, display it (may send unnecessary packets)
                        sendPacketSafe(new DisplayObjectivePacket(slot, obj.getName()));
                    }
                }
            }
            case UPDATE -> sendPacketSafe(packet); // Nothing should be needed here
        }
    }

    public synchronized void sendPacket(@NonNull ScorePacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        sendPacketSafe(packet);
    }

    public synchronized void sendPacket(@NonNull ScoreSetPacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        sendPacketSafe(packet);
    }

    public synchronized void sendPacket(@NonNull ScoreResetPacket packet) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        sendPacketSafe(packet);
    }

    public synchronized void sendPacket(@NonNull TeamPacket packet, @NonNull VelocityTeam affectedTeam) {
        if (viewer.getProtocolVersion().greaterThan(MAXIMUM_SUPPORTED_VERSION)) return;
        switch (packet.getAction()) {
            case REGISTER -> {
                DownstreamTeam team = downstream.getTeam(packet.getName());
                if (team != null) {
                    // Backend is using this team, unregister it to allow this
                    sendPacketSafe(TeamPacket.unregister(packet.getName()));
                }
                sendPacketSafe(packet);
            }
            case UNREGISTER -> {
                sendPacketSafe(packet);
                // Check if backend wanted to display a team with this name
                DownstreamTeam team = downstream.getTeam(packet.getName());
                if (team != null) {
                    // Backend wants this too, send it
                    sendPacketSafe(new TeamPacket(TeamPacket.TeamAction.REGISTER, team.getName(), team.getProperties(), team.getEntryCollection()));
                }

                // Check if removed players belonged to backend teams
                StringCollection teamEntries = affectedTeam.getEntryCollection();
                if (teamEntries.getEntry() != null) {
                    DownstreamTeam backendTeam = downstream.getTeamByEntry(teamEntries.getEntry());
                    if (backendTeam != null) {
                        // Backend team has this player, add back
                        sendPacketSafe(TeamPacket.addOrRemovePlayer(backendTeam.getName(), teamEntries.getEntry(), true));
                    }
                } else {
                    for (String entry : teamEntries.getEntries()) {
                        DownstreamTeam backendTeam = downstream.getTeamByEntry(entry);
                        if (backendTeam != null) {
                            // Backend team has this player, add back
                            sendPacketSafe(TeamPacket.addOrRemovePlayer(backendTeam.getName(), entry, true));
                        }
                    }
                }
            }
            case UPDATE, ADD_PLAYER -> sendPacketSafe(packet); // Nothing should be needed here
            case REMOVE_PLAYER -> {
                sendPacketSafe(packet);

                // Check if backend wanted to display this player
                for (String removedEntry : affectedTeam.getEntryCollection().getEntries()) {
                    DownstreamTeam backendTeam = downstream.getTeamByEntry(removedEntry);
                    if (backendTeam != null) {
                        // Backend team has this player, add back
                        sendPacketSafe(TeamPacket.addOrRemovePlayer(backendTeam.getName(), removedEntry, true));
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

    private synchronized void sendPacketSafe(@NonNull MinecraftPacket packet) {
        if (frozen) return;
        viewer.getConnection().write(packet);
    }

    /**
     * Creates a dump of this scoreboard into a list of lines.
     *
     * @return  dump of this scoreboard
     */
    @NotNull
    public List<String> dump() {
        ArrayList<String> dump = new ArrayList<>();
        dump.add("--- ProxyScoreboard of player " + viewer.getUsername() + " ---");
        dump.add("Teams (" + teams.size() + "):");
        teams.values().forEach(team -> dump.addAll(team.dump()));
        dump.add("Objectives (" + objectives.size() + "):");
        objectives.values().forEach(objective -> dump.addAll(objective.dump()));
        return dump;
    }
}
