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

package com.velocitypowered.proxy.protocol.packet;

import com.google.common.collect.Lists;
import com.velocitypowered.api.scoreboard.CollisionRule;
import com.velocitypowered.api.scoreboard.NameVisibility;
import com.velocitypowered.api.scoreboard.Scoreboard;
import com.velocitypowered.api.scoreboard.Team;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.scoreboard.TeamPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class VelocityTeam implements Team {

    static final int DEFAULT_COLOR = 21;

    @NotNull private final VelocityScoreboard scoreboard;
    @NotNull private final String name;
    @NotNull private Component displayName;
    @NotNull private Component prefix;
    @NotNull private Component suffix;
    @NotNull private NameVisibility nameVisibility;
    @NotNull private CollisionRule collisionRule;
    private int color; // Cannot use NamedTextColor because it does not have ordinals + does not support magic codes or even reset
    boolean allowFriendlyFire;
    boolean canSeeFriendlyInvisibles;
    @NotNull private final Collection<String> entries;
    private boolean registered = true;

    private VelocityTeam(@NotNull VelocityScoreboard scoreboard, @NotNull String name, @NotNull Component displayName,
                         @NotNull Component prefix, @NotNull Component suffix, @NotNull NameVisibility nameVisibility,
                         @NotNull CollisionRule collisionRule, int color, boolean allowFriendlyFire,
                         boolean canSeeFriendlyInvisibles, @NotNull Collection<String> entries) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.displayName = displayName;
        this.prefix = prefix;
        this.suffix = suffix;
        this.nameVisibility = nameVisibility;
        this.collisionRule = collisionRule;
        this.color = color;
        this.allowFriendlyFire = allowFriendlyFire;
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        this.entries = entries;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Component getDisplayName() {
        return displayName;
    }

    @NotNull
    public Component getPrefix() {
        return prefix;
    }

    @NotNull
    public Component getSuffix() {
        return suffix;
    }

    @NotNull
    public NameVisibility getNameVisibility() {
        return nameVisibility;
    }

    @NotNull
    public CollisionRule getCollisionRule() {
        return collisionRule;
    }

    public int getColor() {
        return color;
    }

    public boolean isAllowFriendlyFire() {
        return allowFriendlyFire;
    }

    public boolean isCanSeeFriendlyInvisibles() {
        return canSeeFriendlyInvisibles;
    }

    @NotNull
    public Collection<String> getEntries() {
        return entries;
    }

    @Override
    public void setDisplayName(@NotNull Component displayName) {
        checkState();
        this.displayName = displayName;
        sendUpdate();
    }

    @Override
    public void setPrefix(@NotNull Component prefix) {
        checkState();
        this.prefix = prefix;
        sendUpdate();
    }

    @Override
    public void setSuffix(@NotNull Component suffix) {
        checkState();
        this.suffix = suffix;
        sendUpdate();
    }

    @Override
    public void setNameVisibility(@NotNull NameVisibility visibility) {
        checkState();
        this.nameVisibility = visibility;
        sendUpdate();
    }

    @Override
    public void setCollisionRule(@NotNull CollisionRule collisionRule) {
        checkState();
        this.collisionRule = collisionRule;
        sendUpdate();
    }

    @Override
    public void setColor(int color) {
        checkState();
        this.color = color;
        sendUpdate();
    }

    @Override
    public void setAllowFriendlyFire(boolean friendlyFire) {
        checkState();
        this.allowFriendlyFire = friendlyFire;
        sendUpdate();
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        checkState();
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        sendUpdate();
    }

    @Override
    public void addEntry(@NotNull String entry) {
        checkState();
        if (entries.add(entry)) {
            sendModifyEntry(entry, true);
        } else {
            throw new IllegalArgumentException("This entry is already in the team");
        }
    }

    @Override
    public void removeEntry(@NotNull String entry) {
        checkState();
        if (entries.remove(entry)) {
            sendModifyEntry(entry, false);
        } else {
            throw new IllegalArgumentException("This entry is not in the team");
        }
    }

    public void sendRegister() {
        String legacyDisplayName = LegacyComponentSerializer.legacySection().serialize(displayName);
        if (legacyDisplayName.length() > 16) legacyDisplayName = legacyDisplayName.substring(0, 16);
        String legacyPrefix = LegacyComponentSerializer.legacySection().serialize(prefix);
        if (legacyPrefix.length() > 16) legacyPrefix = legacyPrefix.substring(0, 16);
        String legacySuffix = LegacyComponentSerializer.legacySection().serialize(suffix);
        if (legacySuffix.length() > 16) legacySuffix = legacySuffix.substring(0, 16);
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new TeamPacket(scoreboard.getPriority(), TeamPacket.TeamAction.REGISTER, name, legacyDisplayName,
                    new ComponentHolder(player.getProtocolVersion(), displayName), legacyPrefix,
                    new ComponentHolder(player.getProtocolVersion(), prefix), legacySuffix,
                    new ComponentHolder(player.getProtocolVersion(), suffix), nameVisibility, collisionRule, color, getFlags(), entries));
        }
    }

    private void sendUpdate() {
        String legacyDisplayName = LegacyComponentSerializer.legacySection().serialize(displayName);
        if (legacyDisplayName.length() > 16) legacyDisplayName = legacyDisplayName.substring(0, 16);
        String legacyPrefix = LegacyComponentSerializer.legacySection().serialize(prefix);
        if (legacyPrefix.length() > 16) legacyPrefix = legacyPrefix.substring(0, 16);
        String legacySuffix = LegacyComponentSerializer.legacySection().serialize(suffix);
        if (legacySuffix.length() > 16) legacySuffix = legacySuffix.substring(0, 16);
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new TeamPacket(scoreboard.getPriority(), TeamPacket.TeamAction.UPDATE, name, legacyDisplayName,
                    new ComponentHolder(player.getProtocolVersion(), displayName), legacyPrefix,
                    new ComponentHolder(player.getProtocolVersion(), prefix), legacySuffix,
                    new ComponentHolder(player.getProtocolVersion(), suffix), nameVisibility, collisionRule, color, getFlags(), null));
        }
    }

    private void sendModifyEntry(@NotNull String entry, boolean add) {
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(TeamPacket.addOrRemovePlayer(scoreboard.getPriority(), name, entry, add));
        }
    }

    private byte getFlags() {
        byte flags = 0;
        if (allowFriendlyFire) flags += 1;
        if (canSeeFriendlyInvisibles) flags += 2;
        return flags;
    }

    public void unregister() {
        checkState();
        registered = false;
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(TeamPacket.unregister(scoreboard.getPriority(), name));
        }
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This team was unregistered");
    }

    public static class Builder implements Team.Builder {

        private String name;
        private Component displayName;
        @NotNull private Component prefix = Component.empty();
        @NotNull private Component suffix = Component.empty();
        @NotNull private NameVisibility nameVisibility = NameVisibility.ALWAYS;
        @NotNull private CollisionRule collisionRule = CollisionRule.ALWAYS;
        private int color = DEFAULT_COLOR;
        private boolean allowFriendlyFire = true;
        private boolean canSeeFriendlyInvisibles = false;
        @NotNull private Collection<String> entries = Lists.newArrayList();

        public Builder(@NotNull String name) {
            this.name = name;
            this.displayName = Component.text(name);
        }

        @NotNull
        public Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        @NotNull
        public Builder displayName(@NotNull Component displayName) {
            this.displayName = displayName;
            return this;
        }

        @NotNull
        public Builder prefix(@NotNull Component prefix) {
            this.prefix = prefix;
            return this;
        }

        @NotNull
        public Builder suffix(@NotNull Component suffix) {
            this.suffix = suffix;
            return this;
        }

        @NotNull
        public Builder nameVisibility(@NotNull NameVisibility nameVisibility) {
            this.nameVisibility = nameVisibility;
            return this;
        }

        @NotNull
        public Builder collisionRule(@NotNull CollisionRule collisionRule) {
            this.collisionRule = collisionRule;
            return this;
        }

        @NotNull
        public Builder color(int color) {
            this.color = color;
            return this;
        }

        @NotNull
        public Builder allowFriendlyFire(boolean allowFriendlyFire) {
            this.allowFriendlyFire = allowFriendlyFire;
            return this;
        }

        @NotNull
        public Builder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
            this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
            return this;
        }

        @NotNull
        public Builder entries(@NotNull Collection<String> entries) {
            this.entries = entries;
            return this;
        }

        @NotNull
        public Team build(@NotNull Scoreboard scoreboard) {
            return new VelocityTeam(
                    (VelocityScoreboard) scoreboard, name, displayName, prefix, suffix, nameVisibility, collisionRule,
                    color, allowFriendlyFire, canSeeFriendlyInvisibles, entries
            );
        }
    }
}