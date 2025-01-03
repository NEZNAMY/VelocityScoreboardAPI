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

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.scoreboard.ObjectiveEvent;
import com.velocitypowered.api.event.scoreboard.ScoreboardEventSource;
import com.velocitypowered.api.event.scoreboard.TeamEntryEvent;
import com.velocitypowered.api.event.scoreboard.TeamEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.*;
import com.velocitypowered.proxy.data.LoggerManager;
import com.velocitypowered.proxy.data.StringCollection;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a downstream tracker that reflects on what the backend tried to display to the player.
 */
@RequiredArgsConstructor
public class DownstreamScoreboard implements Scoreboard {

    /** Server to call events to */
    private final ScoreboardEventSource eventSource;

    /** Registered objectives on the backend */
    private final Map<String, DownstreamObjective> objectives = new ConcurrentHashMap<>();

    /** Registered teams on the backend */
    private final Map<String, DownstreamTeam> teams = new ConcurrentHashMap<>();

    /** Display slots assigned to objectives */
    private final Map<DisplaySlot, DownstreamObjective> displaySlots = new ConcurrentHashMap<>();

    /** Map of entries and teams they belong to */
    private final Map<String, DownstreamTeam> teamEntries = new ConcurrentHashMap<>();

    /** Viewer this scoreboard view belongs to */
    @NotNull
    private final Player viewer;

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
                DownstreamObjective obj = new DownstreamObjective(
                        packet.getObjectiveName(),
                        packet.getTitle(),
                        packet.getHealthDisplay(),
                        packet.getNumberFormat(),
                        null
                );
                if (objectives.putIfAbsent(packet.getObjectiveName(), obj) != null) {
                    LoggerManager.Fatal.doubleObjectiveRegister(viewer, packet.getObjectiveName());
                    return true;
                } else {
                    eventSource.fireEvent(new ObjectiveEvent.Register(viewer, this, obj));
                }
            }
            case UNREGISTER -> {
                DownstreamObjective removed = objectives.remove(packet.getObjectiveName());
                if (removed == null) {
                    LoggerManager.Silent.unknownObjectiveUnregister(viewer, packet.getObjectiveName());
                    return true;
                }
                displaySlots.entrySet().removeIf(entry -> entry.getValue().getName().equals(packet.getObjectiveName()));
                eventSource.fireEvent(new ObjectiveEvent.Unregister(viewer, this, removed));
            }
            case UPDATE -> {
                DownstreamObjective objective = objectives.get(packet.getObjectiveName());
                if (objective == null) {
                    LoggerManager.Silent.unknownObjectiveUpdate(viewer, packet.getObjectiveName());
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
            LoggerManager.Silent.unknownObjectiveDisplay(viewer, packet.getObjectiveName(), packet.getPosition());
            return true;
        } else {
            DownstreamObjective previous = displaySlots.put(packet.getPosition(), objective);
            if (previous != null) previous.setDisplaySlot(null);
            objective.setDisplaySlot(packet.getPosition());
            eventSource.fireEvent(new ObjectiveEvent.Display(viewer, this, objective, packet.getPosition()));
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
                packet.getDisplayName(), packet.getNumberFormat());
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
                              @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        DownstreamObjective objective = objectives.get(objectiveName);
        if (objective == null) {
            LoggerManager.Warn.unknownObjectiveSetScore(viewer, objectiveName, holder);
            return true;
        } else {
            objective.setScore(holder, value, displayName, numberFormat);
            return false;
        }
    }

    private boolean handleReset(@Nullable String objectiveName, @NotNull String holder) {
        if (objectiveName == null || objectiveName.isEmpty()) {
            for (DownstreamObjective objective : objectives.values()) {
                objective.removeScore(holder);
            }
        } else {
            DownstreamObjective objective = objectives.get(objectiveName);
            if (objective == null) {
                LoggerManager.Warn.unknownObjectiveResetScore(viewer, objectiveName, holder);
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
        StringCollection entries = packet.getEntries();
        if (packet.getAction() == TeamPacket.TeamAction.REGISTER) {
            DownstreamTeam team = new DownstreamTeam(packet.getName(), packet.getProperties(), entries);
            if (teams.putIfAbsent(packet.getName(), team) != null) {
                LoggerManager.Warn.doubleTeamRegister(viewer, packet.getName());
                return true;
            } else {
                eventSource.fireEvent(new TeamEvent.Register(viewer, this, team));
                if (entries.getEntry() != null) {
                    teamEntries.put(entries.getEntry(), team);
                } else {
                    for (String entry : entries.getEntries()) {
                        teamEntries.put(entry, team);
                    }
                }
                return false;
            }
        }
        DownstreamTeam team = teams.get(packet.getName());
        if (team == null) {
            LoggerManager.Warn.unknownTeamAction(viewer, packet.getName(), packet.getAction());
            return true;
        }
        switch (packet.getAction()) {
            case UNREGISTER -> {
                eventSource.fireEvent(new TeamEvent.Unregister(viewer, this, teams.remove(packet.getName())));
                if (team.getEntryCollection().getEntry() != null) {
                    teamEntries.remove(team.getEntryCollection().getEntry());
                } else {
                    for (String entry : team.getEntryCollection().getEntries()) {
                        teamEntries.remove(entry);
                    }
                }
            }
            case UPDATE -> team.setProperties(packet.getProperties());
            case ADD_PLAYER -> {
                for (DownstreamTeam allTeams : teams.values()) {
                    allTeams.getEntryCollection().removeAll(entries);
                }
                team.addEntries(entries);
                if (entries.getEntry() != null) {
                    eventSource.fireEvent(new TeamEntryEvent.Add(viewer, this, team, entries.getEntry()));
                    teamEntries.put(entries.getEntry(), team);
                } else {
                    for (String entry : entries.getEntries()) {
                        eventSource.fireEvent(new TeamEntryEvent.Add(viewer, this, team, entry));
                        teamEntries.put(entry, team);
                    }
                }
            }
            case REMOVE_PLAYER -> {
                team.removeEntries(viewer, entries);
                if (entries.getEntry() != null) {
                    eventSource.fireEvent(new TeamEntryEvent.Remove(viewer, this, team, entries.getEntry()));
                    teamEntries.remove(entries.getEntry());
                } else {
                    for (String entry : entries.getEntries()) {
                        eventSource.fireEvent(new TeamEntryEvent.Remove(viewer, this, team, entry));
                        teamEntries.remove(entry);
                    }
                }
            }
        }
        return false;
    }

    @Override
    @Nullable
    public DownstreamObjective getObjective(@NotNull DisplaySlot displaySlot) {
        return displaySlots.get(displaySlot);
    }

    @Override
    @Nullable
    public DownstreamObjective getObjective(@NotNull String objectiveName) {
        return objectives.get(objectiveName);
    }

    @Override
    @NotNull
    public Collection<? extends Objective> getObjectives() {
        return Collections.unmodifiableCollection(objectives.values());
    }

    @Override
    @Nullable
    public DownstreamTeam getTeam(@NotNull String teamName) {
        return teams.get(teamName);
    }

    @Override
    @NotNull
    public Collection<? extends Team> getTeams() {
        return Collections.unmodifiableCollection(teams.values());
    }

    /**
     * Returns team the specified entry belongs to. If it does not belong to any,
     * {@code null} is returned.
     *
     * @param   entry
     *          Entry to get team of
     * @return  Team where this entry belongs or {@code null} if none
     */
    @Nullable
    public DownstreamTeam getTeamByEntry(@NotNull String entry) {
        return teamEntries.get(entry);
    }

    /**
     * Clears this scoreboard on server switch when JoinGame packet is received.
     */
    public void clear() {
        objectives.clear();
        teams.clear();
        displaySlots.clear();
        teamEntries.clear();
    }

    /**
     * Creates a dump of this scoreboard into a list of lines.
     *
     * @return  dump of this scoreboard
     */
    @NotNull
    public List<String> dump() {
        ArrayList<String> dump = new ArrayList<>();
        dump.add("--- DownstreamScoreboard of player " + viewer.getUsername() + " ---");
        dump.add("Teams (" + teams.size() + "):");
        teams.values().forEach(team -> dump.addAll(team.dump()));
        dump.add("Objectives (" + objectives.size() + "):");
        objectives.values().forEach(objective -> dump.addAll(objective.dump()));
        return dump;
    }

    public void upload(@NotNull CommandSource sender) throws Exception {
        String contentString = String.join("\n", dump()) + "\n";

        URL url = new URL("https://api.pastes.dev/post");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/log; charset=UTF-8");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(contentString.getBytes(StandardCharsets.UTF_8));
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();

        String responseString = response.toString();
        String id = responseString.substring(responseString.indexOf("\"key\":\"") + 7, responseString.indexOf("\"", responseString.indexOf("\"key\":\"") + 7));

        TextComponent message = Component.text("Click here to open the result.");
        message = message.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://pastes.dev/" + id));
        sender.sendMessage(message);
    }
}
