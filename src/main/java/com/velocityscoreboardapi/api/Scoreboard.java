package com.velocityscoreboardapi.api;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Scoreboard {

    @NotNull
    Objective registerNewObjective(@NonNull String name);

    @NotNull
    Objective registerNewObjective(@NonNull String name, @NonNull Component title, @NonNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat);

    @Nullable
    Objective getObjective(@NonNull String name);

    void unregisterObjective(@NonNull Objective objective);
}
