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

import com.google.common.collect.Lists;
import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.scoreboard.CollisionRule;
import com.velocitypowered.api.scoreboard.NameVisibility;
import com.velocitypowered.api.scoreboard.Scoreboard;
import com.velocitypowered.api.scoreboard.Team;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class VelocityTeam implements Team {

    static final int DEFAULT_COLOR = 21;

    @NotNull private final VelocityScoreboard scoreboard;
    @NotNull private final String name;
    @NotNull private TextHolder displayName;
    @NotNull private TextHolder prefix;
    @NotNull private TextHolder suffix;
    @NotNull private NameVisibility nameVisibility;
    @NotNull private CollisionRule collisionRule;
    private int color; // Cannot use NamedTextColor because it does not have ordinals + does not support magic codes or even reset
    boolean allowFriendlyFire;
    boolean canSeeFriendlyInvisibles;
    @NotNull private final Collection<String> entries;
    private boolean registered = true;

    private VelocityTeam(@NotNull VelocityScoreboard scoreboard, @NotNull String name, @NotNull TextHolder displayName,
                         @NotNull TextHolder prefix, @NotNull TextHolder suffix, @NotNull NameVisibility nameVisibility,
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
    public TextHolder getDisplayName() {
        return displayName;
    }

    @NotNull
    public TextHolder getPrefix() {
        return prefix;
    }

    @NotNull
    public TextHolder getSuffix() {
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
    public void setDisplayName(@NotNull TextHolder displayName) {
        checkState();
        if (this.displayName == displayName) return;
        this.displayName = displayName;
        sendUpdate();
    }

    @Override
    public void setPrefix(@NotNull TextHolder prefix) {
        checkState();
        if (this.prefix == prefix) return;
        this.prefix = prefix;
        sendUpdate();
    }

    @Override
    public void setSuffix(@NotNull TextHolder suffix) {
        checkState();
        if (this.suffix == suffix) return;
        this.suffix = suffix;
        sendUpdate();
    }

    @Override
    public void setNameVisibility(@NotNull NameVisibility visibility) {
        checkState();
        if (this.nameVisibility == visibility) return;
        this.nameVisibility = visibility;
        sendUpdate();
    }

    @Override
    public void setCollisionRule(@NotNull CollisionRule collisionRule) {
        checkState();
        if (this.collisionRule == collisionRule) return;
        this.collisionRule = collisionRule;
        sendUpdate();
    }

    @Override
    public void setColor(int color) {
        checkState();
        if (this.color == color) return;
        this.color = color;
        sendUpdate();
    }

    @Override
    public void setAllowFriendlyFire(boolean friendlyFire) {
        checkState();
        if (this.allowFriendlyFire == friendlyFire) return;
        this.allowFriendlyFire = friendlyFire;
        sendUpdate();
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        checkState();
        if (this.canSeeFriendlyInvisibles == canSeeFriendlyInvisibles) return;
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        sendUpdate();
    }

    @Override
    public void addEntry(@NotNull String entry) {
        checkState();
        if (entries.contains(entry)) return;
        for (VelocityTeam allTeams : scoreboard.getAllTeams()) {
            allTeams.entries.remove(entry);
        }
        entries.add(entry);
        sendModifyEntry(entry, true);
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

    public void sendRegister(@NotNull Collection<ConnectedPlayer> players) {
        for (ConnectedPlayer player : players) {
            player.getConnection().write(new TeamPacket(scoreboard.getPriority(), TeamPacket.TeamAction.REGISTER, name,
                    displayName, prefix, suffix, nameVisibility, collisionRule, color, getFlags(), entries));
        }
    }

    private void sendUpdate() {
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new TeamPacket(scoreboard.getPriority(), TeamPacket.TeamAction.UPDATE, name,
                    displayName, prefix, suffix, nameVisibility, collisionRule, color, getFlags(), null));
        }
    }

    public void sendUnregister(@NotNull Collection<ConnectedPlayer> players) {
        for (ConnectedPlayer player : players) {
            player.getConnection().write(TeamPacket.unregister(scoreboard.getPriority(), name));
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
        sendUnregister(scoreboard.getPlayers());
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This team was unregistered");
    }

    public static class Builder implements Team.Builder {

        @NotNull private final String name;
        @NotNull private TextHolder displayName;
        @NotNull private TextHolder prefix = TextHolder.EMPTY;
        @NotNull private TextHolder suffix = TextHolder.EMPTY;
        @NotNull private NameVisibility nameVisibility = NameVisibility.ALWAYS;
        @NotNull private CollisionRule collisionRule = CollisionRule.ALWAYS;
        private int color = DEFAULT_COLOR;
        private boolean allowFriendlyFire = true;
        private boolean canSeeFriendlyInvisibles = false;
        @NotNull private Collection<String> entries = Lists.newArrayList();

        public Builder(@NotNull String name) {
            this.name = name;
            this.displayName = new TextHolder(name);
        }

        @NotNull
        @Override
        public Builder displayName(@NotNull TextHolder displayName) {
            this.displayName = displayName;
            return this;
        }

        @NotNull
        @Override
        public Builder prefix(@NotNull TextHolder prefix) {
            this.prefix = prefix;
            return this;
        }

        @NotNull
        @Override
        public Builder suffix(@NotNull TextHolder suffix) {
            this.suffix = suffix;
            return this;
        }

        @NotNull
        @Override
        public Builder nameVisibility(@NotNull NameVisibility nameVisibility) {
            this.nameVisibility = nameVisibility;
            return this;
        }

        @NotNull
        @Override
        public Builder collisionRule(@NotNull CollisionRule collisionRule) {
            this.collisionRule = collisionRule;
            return this;
        }

        @NotNull
        @Override
        public Builder color(int color) {
            this.color = color;
            return this;
        }

        @NotNull
        @Override
        public Builder allowFriendlyFire(boolean allowFriendlyFire) {
            this.allowFriendlyFire = allowFriendlyFire;
            return this;
        }

        @NotNull
        @Override
        public Builder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
            this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
            return this;
        }

        @NotNull
        @Override
        public Builder entries(@NotNull Collection<String> entries) {
            this.entries = entries;
            return this;
        }

        @NotNull
        @Override
        public Team build(@NotNull Scoreboard scoreboard) {
            return new VelocityTeam(
                    (VelocityScoreboard) scoreboard, name, displayName, prefix, suffix, nameVisibility, collisionRule,
                    color, allowFriendlyFire, canSeeFriendlyInvisibles, entries
            );
        }
    }
}
