/*
 * This file is part of VelocityScoreboardAPI, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) NEZNAMY <n.e.z.n.a.m.y@azet.sk>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.velocitypowered.proxy.scoreboard;

import com.velocitypowered.api.event.scoreboard.ObjectiveEvent;
import com.velocitypowered.api.scoreboard.*;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.DisplayObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VelocityObjective implements Objective {

    @NotNull private final VelocityScoreboard scoreboard;
    @NotNull private final String name;
    @NotNull private Component title;
    @NotNull private HealthDisplay healthDisplay;
    @Nullable private NumberFormat numberFormat;
    @Nullable private DisplaySlot displaySlot;
    private boolean registered = true;
    private final Map<String, VelocityScore> scores = new ConcurrentHashMap<>();

    private VelocityObjective(@NotNull VelocityScoreboard scoreboard, @NotNull String name, @NotNull Component title,
                             @NotNull HealthDisplay healthDisplay, @Nullable NumberFormat numberFormat, @Nullable DisplaySlot displaySlot) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.title = title;
        this.healthDisplay = healthDisplay;
        this.numberFormat = numberFormat;
        this.displaySlot = displaySlot;
    }

    @NotNull
    public VelocityScoreboard getScoreboard() {
        return scoreboard;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return title;
    }

    @NotNull
    @Override
    public HealthDisplay getHealthDisplay() {
        return healthDisplay;
    }

    @Nullable
    @Override
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    @Nullable
    @Override
    public DisplaySlot getDisplaySlot() {
        return displaySlot;
    }

    @Override
    public void setDisplaySlot(@NotNull DisplaySlot displaySlot) {
        checkState();
        scoreboard.getProxyServer().getEventManager().fireAndForget(new ObjectiveEvent.Display(name, displaySlot));
        this.displaySlot = displaySlot;
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new DisplayObjectivePacket(scoreboard.getPriority(), displaySlot, name));
        }
    }

    @Override
    public void setTitle(@NotNull Component title) {
        checkState();
        this.title = title;
        sendUpdate();
    }

    @Override
    public void setHealthDisplay(@NotNull HealthDisplay healthDisplay) {
        checkState();
        this.healthDisplay = healthDisplay;
        sendUpdate();
    }

    @Override
    public void setNumberFormat(@Nullable NumberFormat numberFormat) {
        checkState();
        this.numberFormat = numberFormat;
        sendUpdate();
    }

    @Override
    @NotNull
    public Score.Builder scoreBuilder(@NotNull String holder) {
        return new VelocityScore.Builder(holder);
    }

    @Override
    @NotNull
    public Score registerScore(@NotNull Score.Builder builder) {
        checkState();
        VelocityScore score = (VelocityScore) builder.build(this);
        scores.put(score.getHolder(), score);
        score.sendUpdate(scoreboard.getPlayers());
        return score;
    }

    @Override
    @NotNull
    public Score getScore(@NotNull String name) {
        checkState();
        return scores.get(name);
    }

    @Override
    public void removeScore(@NotNull String name) {
        checkState();
        VelocityScore score = scores.get(name);
        if (score == null) throw new IllegalArgumentException("Score \"" + name + "\" is not in this objective");
        score.remove();
        scores.remove(name);
    }

    @Override
    public void removeScore(@NotNull Score score) {
        checkState();
        if (!((VelocityScore)score).isRegistered()) throw new IllegalStateException("This score has already been unregistered");
        ((VelocityScore) score).remove();
        scores.remove(score.getHolder());
    }

    public void sendRegister(@NotNull Collection<ConnectedPlayer> players) {
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(title);
        if (legacyTitle.length() > 32) legacyTitle = legacyTitle.substring(0, 32);
        scoreboard.getProxyServer().getEventManager().fireAndForget(new ObjectiveEvent.Register(name));
        for (ConnectedPlayer player : players) {
            player.getConnection().write(new ObjectivePacket(
                    scoreboard.getPriority(),
                    ObjectivePacket.ObjectiveAction.REGISTER,
                    name,
                    legacyTitle,
                    new ComponentHolder(player.getProtocolVersion(), title),
                    healthDisplay,
                    numberFormat
            ));
            if (displaySlot != null) {
                player.getConnection().write(new DisplayObjectivePacket(scoreboard.getPriority(), displaySlot, name));
            }
        }
    }

    private void sendUpdate() {
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(title);
        if (legacyTitle.length() > 32) legacyTitle = legacyTitle.substring(0, 32);
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new ObjectivePacket(
                    scoreboard.getPriority(),
                    ObjectivePacket.ObjectiveAction.UPDATE,
                    name,
                    legacyTitle,
                    new ComponentHolder(player.getProtocolVersion(), title),
                    healthDisplay,
                    numberFormat
            ));
        }
    }

    public void sendUnregister(@NotNull Collection<ConnectedPlayer> players) {
        scoreboard.getProxyServer().getEventManager().fireAndForget(new ObjectiveEvent.Unregister(name));
        for (ConnectedPlayer player : players) {
            player.getConnection().write(new ObjectivePacket(
                    scoreboard.getPriority(),
                    ObjectivePacket.ObjectiveAction.UNREGISTER,
                    name,
                    "",
                    new ComponentHolder(player.getProtocolVersion(), title),
                    HealthDisplay.INTEGER,
                    null
            ));
        }
    }

    public void unregister() {
        checkState();
        registered = false;
        sendUnregister(scoreboard.getPlayers());
    }

    @NotNull
    public Collection<VelocityScore> getScores() {
        return scores.values();
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This objective was unregistered");
    }

    public static class Builder extends NumberFormatProvider.Builder implements Objective.Builder {

        @NotNull private final String name;
        @NotNull private Component title;
        @NotNull private HealthDisplay healthDisplay = HealthDisplay.INTEGER;
        @Nullable private DisplaySlot displaySlot = null;

        public Builder(@NotNull String name) {
            if (name.length() > 16) throw new IllegalArgumentException("Objective name cannot be longer than 16 characters (was " + name.length() + ": " + name + ")");
            this.name = name;
            this.title = Component.text(name);
        }

        @Override
        @NotNull
        public Builder title(@NotNull Component title) {
            this.title = title;
            return this;
        }

        @Override
        @NotNull
        public Builder healthDisplay(@NotNull HealthDisplay healthDisplay) {
            this.healthDisplay = healthDisplay;
            return this;
        }

        @Override
        @NotNull
        public Objective.Builder displaySlot(@NotNull DisplaySlot displaySlot) {
            this.displaySlot = displaySlot;
            return this;
        }

        @Override
        @NotNull
        public VelocityObjective build(@NotNull Scoreboard scoreboard) {
            return new VelocityObjective((VelocityScoreboard) scoreboard, name, title, healthDisplay, numberFormat, displaySlot);
        }

    }

}
