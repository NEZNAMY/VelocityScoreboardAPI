package com.velocityscoreboardapi.api;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Scoreboard {

    @NotNull
    Objective registerNewObjective(@NonNull String name);

    @NotNull
    Objective registerNewObjective(@NonNull String name, @NonNull Component title, @NonNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat);

    @Nullable
    Objective getObjective(@NonNull String name);

    void unregisterObjective(@NonNull String objectiveName);

    @NotNull
    Team registerNewTeam(@NonNull String teamName);

    @NotNull
    Team registerNewTeam(@NonNull String teamName, @NonNull Component displayName, @NonNull Component prefix, @NonNull Component suffix,
                         @NonNull NameVisibility nameVisibility, @NonNull CollisionRule collisionRule, int color, boolean allowFriendlyFire,
                         boolean canSeeFriendlyInvisibles, @Nullable Collection<String> entries);

    @Nullable
    Team getTeam(@NonNull String teamName);

    void unregisterTeam(@NonNull String teamName);
}
