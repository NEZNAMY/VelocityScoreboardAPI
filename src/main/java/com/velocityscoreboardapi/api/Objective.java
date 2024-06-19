package com.velocityscoreboardapi.api;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Objective {

    @Nullable
    DisplaySlot getDisplaySlot();

    void setDisplaySlot(@NonNull DisplaySlot displaySlot);

    @NotNull
    String getName();

    @NotNull
    Component getTitle();

    void setTitle(@NonNull Component title);

    @NotNull
    HealthDisplay getHealthDisplay();

    void setHealthDisplay(@NonNull HealthDisplay healthDisplay);

    @Nullable
    NumberFormat getNumberFormat();

    void setNumberFormat(@NonNull NumberFormat numberFormat);

    @NotNull
    Score findOrCreateScore(@NonNull String name);

    @NotNull
    Score findOrCreateScore(@NonNull String name, int value, @Nullable Component displayName, @Nullable NumberFormat numberFormat);

    void removeScore(@NonNull String name);

    void removeScore(@NonNull Score score);

    interface Builder {

        @NotNull
        Builder title(@NotNull Component title);

        @NotNull
        Builder healthDisplay(@NotNull HealthDisplay healthDisplay);

        @NotNull
        Builder numberFormat(@Nullable NumberFormat numberFormat);

        @NotNull
        Objective build();

    }
}
