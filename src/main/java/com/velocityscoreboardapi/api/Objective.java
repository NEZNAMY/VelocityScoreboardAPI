package com.velocityscoreboardapi.api;

import com.velocityscoreboardapi.impl.VelocityObjective;
import lombok.NonNull;
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
    Score findOrCreateScore(@NonNull String name, @NonNull Score.Builder builder);

    void removeScore(@NonNull String name);

    void removeScore(@NonNull Score score);

    interface Builder {

        @NotNull
        Builder name(@NonNull String name);

        @NotNull
        Builder title(@NotNull Component title);

        @NotNull
        Builder healthDisplay(@NotNull HealthDisplay healthDisplay);

        @NotNull
        Builder numberFormat(@Nullable NumberFormat numberFormat);

        @NotNull
        Objective build(@NonNull Scoreboard scoreboard);

    }
}
