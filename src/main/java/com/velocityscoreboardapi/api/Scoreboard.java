package com.velocityscoreboardapi.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Scoreboard {

    @NotNull
    Objective registerObjective(@NotNull Objective.Builder builder);

    @Nullable
    Objective getObjective(@NotNull String name);

    void unregisterObjective(@NotNull String objectiveName);

    @NotNull
    Team registerTeam(@NotNull Team.Builder builder);

    @Nullable
    Team getTeam(@NotNull String teamName);

    void unregisterTeam(@NotNull String teamName);

}
