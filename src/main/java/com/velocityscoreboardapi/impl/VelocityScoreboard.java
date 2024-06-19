package com.velocityscoreboardapi.impl;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocityscoreboardapi.api.Objective;
import com.velocityscoreboardapi.api.Scoreboard;
import com.velocityscoreboardapi.api.Team;
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
