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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.scoreboard.*;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public class VelocityTeam implements ProxyTeam {

    @NotNull private final VelocityScoreboard scoreboard;
    @NotNull private final String name;
    @NotNull private final TeamProperties properties;
    @NotNull private final Collection<String> entries;
    private boolean registered = true;

    private VelocityTeam(@NotNull VelocityScoreboard scoreboard, @NotNull String name, @NotNull TeamProperties properties, @NotNull Collection<String> entries) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.properties = properties;
        this.entries = entries;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    @NotNull
    public TextHolder getDisplayName() {
        return properties.getDisplayName();
    }

    @Override
    @NotNull
    public TextHolder getPrefix() {
        return properties.getPrefix();
    }

    @Override
    @NotNull
    public TextHolder getSuffix() {
        return properties.getSuffix();
    }

    @Override
    @NotNull
    public NameVisibility getNameVisibility() {
        return properties.getNameTagVisibility();
    }

    @Override
    @NotNull
    public CollisionRule getCollisionRule() {
        return properties.getCollisionRule();
    }

    @Override
    @NotNull
    public TeamColor getColor() {
        return properties.getColor();
    }

    @Override
    public boolean isAllowFriendlyFire() {
        return properties.isAllowFriendlyFire();
    }

    @Override
    public boolean isCanSeeFriendlyInvisibles() {
        return properties.isCanSeeFriendlyInvisibles();
    }

    @Override
    @NotNull
    public Collection<String> getEntries() {
        return ImmutableSet.copyOf(entries);
    }

    @Override
    public void setDisplayName(@NotNull TextHolder displayName) {
        checkState();
        if (properties.setDisplayName(displayName)) {
            sendUpdate();
        }
    }

    @Override
    public void setPrefix(@NotNull TextHolder prefix) {
        checkState();
        if (properties.setPrefix(prefix)) {
            sendUpdate();
        }
    }

    @Override
    public void setSuffix(@NotNull TextHolder suffix) {
        checkState();
        if (properties.setSuffix(suffix)) {
            sendUpdate();
        }
    }

    @Override
    public void setNameVisibility(@NotNull NameVisibility visibility) {
        checkState();
        if (properties.setNameVisibility(visibility)) {
            sendUpdate();
        }
    }

    @Override
    public void setCollisionRule(@NotNull CollisionRule collisionRule) {
        checkState();
        if (properties.setCollisionRule(collisionRule)) {
            sendUpdate();
        }
    }

    @Override
    public void setColor(@NotNull TeamColor color) {
        checkState();
        if (properties.setColor(color)) {
            sendUpdate();
        }
    }

    @Override
    public void setAllowFriendlyFire(boolean friendlyFire) {
        checkState();
        if (properties.setAllowFriendlyFire(friendlyFire)) {
            sendUpdate();
        }
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        checkState();
        if (properties.setCanSeeFriendlyInvisibles(canSeeFriendlyInvisibles)) {
            sendUpdate();
        }
    }

    @Override
    public void updateProperties(@NotNull Consumer<ProxyTeam.PropertyBuilder> builderConsumer) {
        checkState();
        PropertyBuilder builder = new PropertyBuilder();
        builderConsumer.accept(builder);
        if (builder.displayName != null) properties.setDisplayName(builder.displayName);
        if (builder.prefix != null) properties.setPrefix(builder.prefix);
        if (builder.suffix != null) properties.setSuffix(builder.suffix);
        if (builder.nameVisibility != null) properties.setNameVisibility(builder.nameVisibility);
        if (builder.collisionRule != null) properties.setCollisionRule(builder.collisionRule);
        if (builder.color != null) properties.setColor(builder.color);
        if (builder.allowFriendlyFire != null) properties.setAllowFriendlyFire(builder.allowFriendlyFire);
        if (builder.canSeeFriendlyInvisibles != null) properties.setCanSeeFriendlyInvisibles(builder.canSeeFriendlyInvisibles);
        sendUpdate();
    }

    @Override
    public void addEntry(@NotNull String entry) {
        checkState();
        if (entries.contains(entry)) return;
        for (ProxyTeam allTeams : scoreboard.getTeams()) {
            ((VelocityTeam)allTeams).entries.remove(entry);
        }
        entries.add(entry);
        scoreboard.sendPacket(TeamPacket.addOrRemovePlayer(name, entry, true));
    }

    @Override
    public void removeEntry(@NotNull String entry) throws IllegalArgumentException {
        checkState();
        if (entries.remove(entry)) {
            scoreboard.sendPacket(TeamPacket.addOrRemovePlayer(name, entry, false));
        } else {
            throw new IllegalArgumentException("This entry is not in the team");
        }
    }

    public void removeEntriesRaw(@NotNull Collection<String> entries) {
        this.entries.removeAll(entries);
    }

    public void sendRegister() {
        scoreboard.sendPacket(new TeamPacket(TeamPacket.TeamAction.REGISTER, name, properties, entries));
    }

    private void sendUpdate() {
        scoreboard.sendPacket(new TeamPacket(TeamPacket.TeamAction.UPDATE, name, properties, null));
    }

    public void unregister() {
        checkState();
        scoreboard.sendPacket(TeamPacket.unregister(name));
        registered = false;
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This team was unregistered");
    }

    public static class PropertyBuilder implements ProxyTeam.PropertyBuilder {

        @Nullable protected TextHolder displayName;
        @Nullable protected TextHolder prefix;
        @Nullable protected TextHolder suffix;
        @Nullable protected NameVisibility nameVisibility;
        @Nullable protected CollisionRule collisionRule;
        @Nullable protected TeamColor color;
        @Nullable protected Boolean allowFriendlyFire;
        @Nullable protected Boolean canSeeFriendlyInvisibles;

        @NotNull
        @Override
        public PropertyBuilder displayName(@NotNull TextHolder displayName) {
            this.displayName = displayName;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder prefix(@NotNull TextHolder prefix) {
            this.prefix = prefix;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder suffix(@NotNull TextHolder suffix) {
            this.suffix = suffix;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder nameVisibility(@NotNull NameVisibility nameVisibility) {
            this.nameVisibility = nameVisibility;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder collisionRule(@NotNull CollisionRule collisionRule) {
            this.collisionRule = collisionRule;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder color(@NotNull TeamColor color) {
            this.color = color;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder allowFriendlyFire(boolean allowFriendlyFire) {
            this.allowFriendlyFire = allowFriendlyFire;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
            this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
            return this;
        }
    }

    public static class Builder extends PropertyBuilder implements ProxyTeam.Builder {

        @NotNull private final String name;
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
        public Builder color(@NotNull TeamColor color) {
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

        /**
         * Builds this entry into a team.
         *
         * @param   scoreboard
         *          Scoreboard to register this team into
         * @return  Newly created team
         */
        @NotNull
        public VelocityTeam build(@NotNull VelocityScoreboard scoreboard) {
            if (displayName == null) displayName = new TextHolder(name);
            if (prefix == null) prefix = TextHolder.EMPTY;
            if (suffix == null) suffix = TextHolder.EMPTY;
            if (nameVisibility == null) nameVisibility = NameVisibility.ALWAYS;
            if (collisionRule == null) collisionRule = CollisionRule.ALWAYS;
            if (color == null) color = TeamColor.RESET;
            if (allowFriendlyFire == null) allowFriendlyFire = false;
            if (canSeeFriendlyInvisibles == null) canSeeFriendlyInvisibles = false;
            return new VelocityTeam(scoreboard, name, new TeamProperties(displayName, prefix, suffix, nameVisibility, collisionRule,
                    color, allowFriendlyFire, canSeeFriendlyInvisibles), Sets.newConcurrentHashSet(entries)
            );
        }
    }
}
