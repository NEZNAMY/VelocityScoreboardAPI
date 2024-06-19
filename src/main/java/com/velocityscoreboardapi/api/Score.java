package com.velocityscoreboardapi.api;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Score {

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
}
