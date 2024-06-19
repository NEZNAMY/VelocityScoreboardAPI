package com.velocityscoreboardapi.impl;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocityscoreboardapi.api.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public Objective registerNewObjective(@NonNull String name) {
        return registerNewObjective(name, Component.text(name), HealthDisplay.INTEGER, null);
    }

    @Override
    @NotNull
    public Objective registerNewObjective(@NonNull String name, @NonNull Component title, @NonNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat) {
        if (objectives.containsKey(name)) throw new IllegalArgumentException("Objective with this name already exists");
        if (name.length() > 16) throw new IllegalArgumentException("Objective name cannot be longer than 16 characters (was " + name.length() + ": " + name + ")");
        VelocityObjective objective = new VelocityObjective(this, name, title, healthDisplay, numberFormat);
        objectives.put(name, objective);
        objective.sendRegister();
        return objective;
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

    @Override
    @NotNull
    public Team registerNewTeam(@NonNull String teamName) {
        return registerNewTeam(teamName, Component.text(teamName), Component.empty(), Component.empty(), NameVisibility.ALWAYS,
                CollisionRule.ALWAYS, 21, false, false, null);
    }

    @Override
    @NotNull
    public Team registerNewTeam(@NonNull String teamName, @NonNull Component displayName, @NonNull Component prefix,
                                         @NonNull Component suffix, @NonNull NameVisibility nameVisibility, @NonNull CollisionRule collisionRule,
                                         int color, boolean allowFriendlyFire, boolean canSeeFriendlyInvisibles, @Nullable Collection<String> entries) {
        if (teams.containsKey(teamName)) throw new IllegalArgumentException("Team with this name already exists");
        if (teamName.length() > 16) throw new IllegalArgumentException("Team name cannot be longer than 16 characters (was " + teamName.length() + ": " + teamName + ")");
        VelocityTeam team = new VelocityTeam(this, teamName, displayName, prefix, suffix, nameVisibility, collisionRule, color,
                allowFriendlyFire, canSeeFriendlyInvisibles, entries == null ? new HashSet<>() : new HashSet<>(entries), true);
        teams.put(teamName, team);
        team.sendRegister();
        return team;
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
