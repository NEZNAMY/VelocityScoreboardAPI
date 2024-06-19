package com.velocityscoreboardapi.api;

import com.velocityscoreboardapi.impl.VelocityTeam;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Scoreboard {

    @NotNull
    Objective registerNewObjective(@NonNull String objectiveName, @NonNull Function<Objective.Builder, Objective> build);

    @NotNull
    Objective registerNewObjective(@NonNull String objectiveName);

    @Nullable
    Objective getObjective(@NonNull String name);

    void unregisterObjective(@NonNull String objectiveName);

    @NotNull
    Team registerNewTeam(@NonNull String teamName);

    @NotNull
    Team registerNewTeam(@NonNull String teamName, @NonNull Function<Team.Builder, Team> build);

    @Nullable
    Team getTeam(@NonNull String teamName);

    void unregisterTeam(@NonNull String teamName);

}
