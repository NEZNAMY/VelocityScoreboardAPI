package com.velocityscoreboardapi.impl;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.DisplayObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import com.velocityscoreboardapi.api.DisplaySlot;
import com.velocityscoreboardapi.api.HealthDisplay;
import com.velocityscoreboardapi.api.NumberFormat;
import com.velocityscoreboardapi.api.Objective;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class VelocityObjective implements Objective {

    @NotNull
    private final VelocityScoreboard scoreboard;

    @NotNull
    private final String name;

    @NotNull
    private Component title;

    @NotNull
    private HealthDisplay healthDisplay = HealthDisplay.INTEGER;

    @Nullable
    private NumberFormat numberFormat;

    @Nullable
    private DisplaySlot displaySlot;

    private boolean registered = true;

    public VelocityObjective(@NonNull VelocityScoreboard scoreboard, @NonNull String name) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.title = Component.text(name);
    }

    public VelocityObjective(@NonNull VelocityScoreboard scoreboard, @NonNull String name, @NonNull Component title,
                             @NonNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.title = title;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
    }

    @Override
    public void setDisplaySlot(@NonNull DisplaySlot displaySlot) {
        if (!registered) throw new IllegalStateException("This objective was unregistered");
        this.displaySlot = displaySlot;
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new DisplayObjectivePacket(false, displaySlot.ordinal(), name));
        }
    }

    @Override
    public void setTitle(@NonNull Component title) {
        if (!registered) throw new IllegalStateException("This objective was unregistered");
        this.title = title;
        sendUpdate();
    }

    @Override
    public void setHealthDisplay(@NonNull HealthDisplay healthDisplay) {
        if (!registered) throw new IllegalStateException("This objective was unregistered");
        this.healthDisplay = healthDisplay;
        sendUpdate();
    }

    @Override
    public void setNumberFormat(@Nullable NumberFormat numberFormat) {
        if (!registered) throw new IllegalStateException("This objective was unregistered");
        this.numberFormat = numberFormat;
        sendUpdate();
    }

    public void sendRegister() {
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(title);
        if (legacyTitle.length() > 32) legacyTitle = legacyTitle.substring(0, 32);
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new ObjectivePacket(
                    false,
                    name,
                    legacyTitle,
                    new ComponentHolder(player.getProtocolVersion(), title),
                    healthDisplay,
                    (byte) 0,
                    numberFormat
            ));
        }
    }

    private void sendUpdate() {
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(title);
        if (legacyTitle.length() > 32) legacyTitle = legacyTitle.substring(0, 32);
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new ObjectivePacket(
                    false,
                    name,
                    legacyTitle,
                    new ComponentHolder(player.getProtocolVersion(), title),
                    healthDisplay,
                    (byte) 2,
                    numberFormat
            ));
        }
    }

    public void unregister() {
        if (!registered) throw new IllegalStateException("This objective was unregistered");
        registered = false;
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new ObjectivePacket(
                    false,
                    name,
                    "",
                    new ComponentHolder(player.getProtocolVersion(), title),
                    null,
                    (byte) 1,
                    null
            ));
        }
    }
}
