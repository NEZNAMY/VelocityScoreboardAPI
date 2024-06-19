package com.velocityscoreboardapi.api;

import com.velocityscoreboardapi.impl.VelocityObjective;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Objective {

    @NotNull
    static Builder builder() {
        return new VelocityObjective.Builder();
    }

    @Nullable
    DisplaySlot getDisplaySlot();

    void setDisplaySlot(@NotNull DisplaySlot displaySlot);

    @NotNull
    String getName();

    @NotNull
    Component getTitle();

    void setTitle(@NotNull Component title);

    @NotNull
    HealthDisplay getHealthDisplay();

    void setHealthDisplay(@NotNull HealthDisplay healthDisplay);

    @Nullable
    NumberFormat getNumberFormat();

    void setNumberFormat(@NotNull NumberFormat numberFormat);

    @NotNull
    Score findOrCreateScore(@NotNull String name, @NotNull Score.Builder builder);

    void removeScore(@NotNull String name);

    void removeScore(@NotNull Score score);

    interface Builder {

        @NotNull
        Builder name(@NotNull String name);

        @NotNull
        Builder title(@NotNull Component title);

        @NotNull
        Builder healthDisplay(@NotNull HealthDisplay healthDisplay);

        @NotNull
        Builder numberFormat(@Nullable NumberFormat numberFormat);

        @NotNull
        Objective build(@NotNull Scoreboard scoreboard);

    }
}
