package com.velocityscoreboardapi.api;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Scoreboard {

    @NotNull
    Objective registerObjective(@NonNull Objective.Builder builder);

    @Nullable
    Objective getObjective(@NonNull String name);

    void unregisterObjective(@NonNull String objectiveName);

    @NotNull
    Team registerTeam(@NonNull Team.Builder builder);

    @Nullable
    Team getTeam(@NonNull String teamName);

    void unregisterTeam(@NonNull String teamName);

}
