package com.velocityscoreboardapi.impl;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocityscoreboardapi.api.HealthDisplay;
import com.velocityscoreboardapi.api.NumberFormat;
import com.velocityscoreboardapi.api.Objective;
import com.velocityscoreboardapi.api.Scoreboard;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Getter
public class VelocityScoreboard implements Scoreboard {

    /** Priority of this scoreboard */
    private final int priority;

    @Getter
    private final Collection<ConnectedPlayer> players = new HashSet<>();

    private final Map<String, VelocityObjective> objectives = new ConcurrentHashMap<>();

    @Override
    @NotNull
    public Objective registerNewObjective(@NonNull String name) {
        return registerNewObjective(name, Component.text(name), HealthDisplay.INTEGER, null);
    }

    @Override
    @NotNull
    public Objective registerNewObjective(@NonNull String name, @NonNull Component title, @NonNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat) {
        if (objectives.containsKey(name)) throw new IllegalArgumentException("Objective with this name already exists");
        if (name.length() > 16) throw new IllegalArgumentException("Objective name cannot be longer than 16 characters (was " + name.length() + ": " + name + ")");
        VelocityObjective objective = new VelocityObjective(this, name, title, healthDisplay, numberFormat);
        objectives.put(name, objective);
        objective.sendRegister();
        return objective;
    }

    @Override
    @Nullable
    public Objective getObjective(@NonNull String name) {
        return objectives.get(name);
    }

    @Override
    public void unregisterObjective(@NonNull Objective objective) {
        objectives.remove(objective.getName());
        ((VelocityObjective)objective).unregister();
    }
}
