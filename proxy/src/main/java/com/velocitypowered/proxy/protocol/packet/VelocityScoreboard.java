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

package com.velocitypowered.proxy.protocol.packet;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.Objective;
import com.velocitypowered.api.scoreboard.Scoreboard;
import com.velocitypowered.api.scoreboard.Team;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VelocityScoreboard implements Scoreboard {

    /** Priority of this scoreboard */
    private final int priority;

    private final Collection<ConnectedPlayer> players = new HashSet<>();
    private final Map<String, VelocityObjective> objectives = new ConcurrentHashMap<>();
    private final Map<String, VelocityTeam> teams = new ConcurrentHashMap<>();

    public VelocityScoreboard(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public Collection<ConnectedPlayer> getPlayers() {
        return players;
    }

    @Override
    @NotNull
    public Team.Builder teamBuilder(@NotNull String name) {
        return new VelocityTeam.Builder(name);
    }

    @Override
    @NotNull
    public Objective.Builder objectiveBuilder(@NotNull String name) {
        return new VelocityObjective.Builder(name);
    }

    @Override
    public void addPlayer(@NotNull Player player) {
        getPlayers().add((ConnectedPlayer) player);
    }

    @Override
    @NotNull
    public Objective registerObjective(@NotNull Objective.Builder builder) {
        final VelocityObjective objective = (VelocityObjective) builder.build(this);
        if (objectives.containsKey(objective.getName())) throw new IllegalArgumentException("Objective with this name already exists");
        objectives.put(objective.getName(), objective);
        objective.sendRegister();
        return objective;
    }

    @Override
    @Nullable
    public Objective getObjective(@NotNull String name) {
        return objectives.get(name);
    }

    @Override
    public void unregisterObjective(@NotNull String objectiveName) {
        VelocityObjective obj = objectives.remove(objectiveName);
        if (obj == null) throw new IllegalStateException("This scoreboard does not contain objective named " + objectiveName);
        obj.unregister();
    }

    @NotNull
    @Override
    public Team registerTeam(@NotNull Team.Builder builder) {
        final VelocityTeam team = (VelocityTeam) builder.build(this);
        if (teams.containsKey(team.getName())) throw new IllegalArgumentException("Team with this name already exists");
        teams.put(team.getName(), team);
        team.sendRegister();
        return team;
    }

    @Override
    @Nullable
    public Team getTeam(@NotNull String teamName) {
        return teams.get(teamName);
    }

    @Override
    public void unregisterTeam(@NotNull String teamName) {
        VelocityTeam team = teams.remove(teamName);
        if (team == null) throw new IllegalStateException("This scoreboard does not contain team named " + teamName);
        team.unregister();
    }
}
