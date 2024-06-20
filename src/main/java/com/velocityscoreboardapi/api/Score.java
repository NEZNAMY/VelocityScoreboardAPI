package com.velocityscoreboardapi.api;

import com.velocityscoreboardapi.impl.VelocityScore;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Score {

    @NotNull
    static Builder builder(@NotNull String holder) {
        return new VelocityScore.Builder(holder);
    }

    @NotNull
    String getHolder();

    int getScore();

    void setScore(int score);

    @Nullable
    Component getDisplayName();

    void setDisplayName(@Nullable Component displayName);

    @Nullable
    NumberFormat getNumberFormat();

    void setNumberFormat(@Nullable NumberFormat numberFormat);

    interface Builder {

        @NotNull
        Builder holder(@NotNull String holder);

        @NotNull
        Builder score(int score);

        @NotNull
        Builder displayName(@Nullable Component displayName);

        @NotNull
        Builder numberFormat(@Nullable NumberFormat numberFormat);

        @NotNull
        Score build(@NotNull Objective objective);

    }

}
