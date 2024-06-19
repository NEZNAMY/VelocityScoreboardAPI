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
}
