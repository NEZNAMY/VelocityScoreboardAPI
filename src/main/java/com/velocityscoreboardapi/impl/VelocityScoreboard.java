package com.velocityscoreboardapi.impl;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocityscoreboardapi.api.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public class VelocityScoreboard implements Scoreboard {

    /** Priority of this scoreboard */
    private final int priority;

    @Getter
    private final Collection<ConnectedPlayer> players = new HashSet<>();
    private final Map<String, VelocityObjective> objectives = new ConcurrentHashMap<>();
    private final Map<String, VelocityTeam> teams = new ConcurrentHashMap<>();

    @Override
    @NotNull
    public Objective registerNewObjective(@NonNull String objectiveName, @NonNull Function<Objective.Builder, Objective> build) {
        if (objectives.containsKey(objectiveName)) throw new IllegalArgumentException("Objective with this name already exists");
        final VelocityObjective objective = (VelocityObjective) build.apply(new VelocityObjective.Builder(objectiveName, this));
        objectives.put(objectiveName, objective);
        objective.sendRegister();
        return objective;
    }

    @Override
    @NotNull
    public Objective registerNewObjective(@NonNull String name) {
        return registerNewObjective(name, Objective.Builder::build);
    }

    @Override
    @Nullable
    public Objective getObjective(@NonNull String name) {
        return objectives.get(name);
    }

    @Override
    public void unregisterObjective(@NonNull String objectiveName) {
        VelocityObjective obj = objectives.remove(objectiveName);
        if (obj == null) throw new IllegalStateException("This scoreboard does not contain objective named " + objectiveName);
        obj.unregister();
    }

    @NonNull
    public Team registerNewTeam(@NonNull String teamName, @NonNull Function<Team.Builder, Team> build) {
        if (teams.containsKey(teamName)) throw new IllegalArgumentException("Team with this name already exists");
        final VelocityTeam team = (VelocityTeam) build.apply(new VelocityTeam.Builder(teamName, this));
        teams.put(teamName, team);
        team.sendRegister();
        return team;
    }

    @Override
    @NotNull
    public Team registerNewTeam(@NonNull String teamName) {
        return registerNewTeam(teamName, Team.Builder::build);
    }

    @Override
    @Nullable
    public Team getTeam(@NonNull String teamName) {
        return teams.get(teamName);
    }

    @Override
    public void unregisterTeam(@NonNull String teamName) {
        VelocityTeam team = teams.remove(teamName);
        if (team == null) throw new IllegalStateException("This scoreboard does not contain team named " + teamName);
        team.unregister();
    }
}
