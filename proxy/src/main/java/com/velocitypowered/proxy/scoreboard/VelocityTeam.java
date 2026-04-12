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

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.proxy.ScoreboardEventSource;
import com.velocitypowered.api.event.scoreboard.TeamEvent;
import com.velocitypowered.api.scoreboard.CollisionRule;
import com.velocitypowered.api.scoreboard.NameVisibility;
import com.velocitypowered.api.scoreboard.ProxyTeam;
import com.velocitypowered.api.scoreboard.TeamColor;
import com.velocitypowered.proxy.data.StringCollection;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VelocityTeam implements ProxyTeam {

    @NonNull private final VelocityScoreboard scoreboard;
    @NonNull private final String name;
    @NonNull private final TeamProperties properties;
    @NonNull private final StringCollection entries;
    private boolean registered = true;

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
        return properties.getNameVisibility();
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
        return Collections.unmodifiableCollection(entries.getEntries());
    }

    @NotNull
    public StringCollection getEntryCollection() {
        return entries;
    }

    @Override
    public void setDisplayName(@NonNull TextHolder displayName) {
        PropertyBuilder builder = new PropertyBuilder();
        builder.displayName(displayName);
        tryUpdate(builder);
    }

    @Override
    public void setPrefix(@NonNull TextHolder prefix) {
        PropertyBuilder builder = new PropertyBuilder();
        builder.prefix(prefix);
        tryUpdate(builder);
    }

    @Override
    public void setSuffix(@NonNull TextHolder suffix) {
        PropertyBuilder builder = new PropertyBuilder();
        builder.suffix(suffix);
        tryUpdate(builder);
    }

    @Override
    public void setNameVisibility(@NonNull NameVisibility visibility) {
        PropertyBuilder builder = new PropertyBuilder();
        builder.nameVisibility(visibility);
        tryUpdate(builder);
    }

    @Override
    public void setCollisionRule(@NonNull CollisionRule collisionRule) {
        PropertyBuilder builder = new PropertyBuilder();
        builder.collisionRule(collisionRule);
        tryUpdate(builder);
    }

    @Override
    public void setColor(@NonNull TeamColor color) {
        PropertyBuilder builder = new PropertyBuilder();
        builder.color(color);
        tryUpdate(builder);
    }

    @Override
    public void setAllowFriendlyFire(boolean friendlyFire) {
        PropertyBuilder builder = new PropertyBuilder();
        builder.allowFriendlyFire(friendlyFire);
        tryUpdate(builder);
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        PropertyBuilder builder = new PropertyBuilder();
        builder.canSeeFriendlyInvisibles(canSeeFriendlyInvisibles);
        tryUpdate(builder);
    }

    @Override
    public void updateProperties(@NonNull Consumer<ProxyTeam.PropertyBuilder> builderConsumer) {
        PropertyBuilder builder = new PropertyBuilder();
        builderConsumer.accept(builder);
        tryUpdate(builder);
    }

    private void tryUpdate(@NonNull PropertyBuilder builder) {
        checkState();
        TeamEvent.Update event = new TeamEvent.Update(
                scoreboard.getViewer(),
                true,
                name,
                builder.displayName != null ? builder.displayName : properties.getDisplayName(),
                builder.prefix != null ? builder.prefix : properties.getPrefix(),
                builder.suffix != null ? builder.suffix : properties.getSuffix(),
                builder.nameVisibility != null ? builder.nameVisibility : properties.getNameVisibility(),
                builder.collisionRule != null ? builder.collisionRule : properties.getCollisionRule(),
                builder.color != null ? builder.color : properties.getColor(),
                builder.allowFriendlyFire != null ? builder.allowFriendlyFire : properties.isAllowFriendlyFire(),
                builder.canSeeFriendlyInvisibles != null ? builder.canSeeFriendlyInvisibles : properties.isCanSeeFriendlyInvisibles()
        );
        scoreboard.getEventSource().fireEvent(event);

        boolean changed = false;
        changed |= properties.setDisplayName(event.getDisplayName());
        changed |= properties.setPrefix(event.getPrefix());
        changed |= properties.setSuffix(event.getSuffix());
        changed |= properties.setNameVisibility(event.getNameVisibility());
        changed |= properties.setCollisionRule(event.getCollisionRule());
        changed |= properties.setColor(event.getColor());
        changed |= properties.setAllowFriendlyFire(event.isAllowFriendlyFire());
        changed |= properties.setCanSeeFriendlyInvisibles(event.isCanSeeFriendlyInvisibles());
        if (changed) {
            sendUpdate();
        }
    }

    @Override
    public void addEntry(@NonNull String entry) {
        checkState();
        TeamEvent.AddPlayers event = new TeamEvent.AddPlayers(scoreboard.getViewer(), true, name, Collections.singleton(entry));
        scoreboard.getEventSource().fireEvent(event);
        for (String entry0 : event.getEntries()) {
            if (entries.contains(entry0)) continue;
            VelocityTeam oldTeam = scoreboard.addEntryToTeam(entry0, this);
            if (oldTeam != null) {
                oldTeam.entries.remove(entry0);
            }
            entries.add(entry0);
            // Maybe merge packets into one?
            scoreboard.sendPacket(TeamPacket.addOrRemovePlayer(name, entry0, true), this);
        }
    }

    @Override
    public void removeEntry(@NonNull String entry) throws IllegalArgumentException {
        checkState();
        TeamEvent.RemovePlayers event = new TeamEvent.RemovePlayers(scoreboard.getViewer(), true, name,  Collections.singleton(entry));
        scoreboard.getEventSource().fireEvent(event);
        for (String entry0 : event.getEntries()) {
            if (entries.remove(entry0)) {
                scoreboard.removeEntryFromTeam(entry0, this);
                // Maybe merge packets into one?
                scoreboard.sendPacket(TeamPacket.addOrRemovePlayer(name, entry0, false), this);
            } else {
                // What if it was modified in event and caller is innocent?
                throw new IllegalArgumentException("Entry " + entry0 + " is not in team " + name + ", cannot remove");
            }
        }
    }

    @ApiStatus.Internal
    public void removeEntrySilent(@NonNull String entry) {
        entries.remove(entry);
    }

    public void sendRegister() {
        scoreboard.sendPacket(new TeamPacket(TeamPacket.TeamAction.REGISTER, name, properties, entries), this);
    }

    private void sendUpdate() {
        scoreboard.sendPacket(new TeamPacket(TeamPacket.TeamAction.UPDATE, name, properties, null), this);
    }

    public void unregister() {
        checkState();
        if (entries.getEntry() != null) {
            scoreboard.removeEntryFromTeam(entries.getEntry(), this);
        } else {
            for (String entry : entries.getEntries()) {
                scoreboard.removeEntryFromTeam(entry, this);
            }
        }
        scoreboard.sendPacket(TeamPacket.unregister(name), this);
        scoreboard.getEventSource().fireEvent(new TeamEvent.Unregister(scoreboard.getViewer(), true, name));
        registered = false;
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This team (" + name + ") was unregistered");
    }

    /**
     * Creates a dump of this team into a list of lines.
     *
     * @return  dump of this team
     */
    @NotNull
    public List<String> dump() {
        List<String> content = new ArrayList<>();
        content.add("  " + name + ":");
        content.add("    DisplayName: " + properties.getDisplayName());
        content.add("    Prefix: " + properties.getPrefix());
        content.add("    Suffix: " + properties.getSuffix());
        content.add("    Entries: " + entries);
        return content;
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
        public PropertyBuilder displayName(@NonNull TextHolder displayName) {
            this.displayName = displayName;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder prefix(@NonNull TextHolder prefix) {
            this.prefix = prefix;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder suffix(@NonNull TextHolder suffix) {
            this.suffix = suffix;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder nameVisibility(@NonNull NameVisibility nameVisibility) {
            this.nameVisibility = nameVisibility;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder collisionRule(@NonNull CollisionRule collisionRule) {
            this.collisionRule = collisionRule;
            return this;
        }

        @NotNull
        @Override
        public PropertyBuilder color(@NonNull TeamColor color) {
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

    @Getter
    @RequiredArgsConstructor
    public static class Builder extends PropertyBuilder implements ProxyTeam.Builder {

        @NonNull private final String name;
        @Nullable private Collection<String> entries;

        @NotNull
        @Override
        public Builder displayName(@NonNull TextHolder displayName) {
            this.displayName = displayName;
            return this;
        }

        @NotNull
        @Override
        public Builder prefix(@NonNull TextHolder prefix) {
            this.prefix = prefix;
            return this;
        }

        @NotNull
        @Override
        public Builder suffix(@NonNull TextHolder suffix) {
            this.suffix = suffix;
            return this;
        }

        @NotNull
        @Override
        public Builder nameVisibility(@NonNull NameVisibility nameVisibility) {
            this.nameVisibility = nameVisibility;
            return this;
        }

        @NotNull
        @Override
        public Builder collisionRule(@NonNull CollisionRule collisionRule) {
            this.collisionRule = collisionRule;
            return this;
        }

        @NotNull
        @Override
        public Builder color(@NonNull TeamColor color) {
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
        public Builder entry(@NonNull String entry) {
            this.entries = Collections.singleton(entry);
            return this;
        }

        @NotNull
        @Override
        public Builder entries(@NonNull Collection<String> entries) {
            this.entries = entries;
            return this;
        }

        /**
         * Fires register event, applies modifications, creates the team and returns it.
         *
         * @param   eventSource
         *          Event source to fire event on
         * @param   scoreboard
         *          Scoreboard to create the team on
         * @return  Created team
         */
        @NotNull
        public VelocityTeam callEventAndBuild(@NonNull ScoreboardEventSource eventSource, @NonNull VelocityScoreboard scoreboard) {
            TeamEvent.Register registerEvent = new TeamEvent.Register(
                    scoreboard.getViewer(),
                    true,
                    name,
                    displayName != null ? displayName : TextHolder.of(name),
                    prefix != null ? prefix : TextHolder.empty(),
                    suffix != null ? suffix : TextHolder.empty(),
                    nameVisibility != null ? nameVisibility : NameVisibility.ALWAYS,
                    collisionRule != null ? collisionRule : CollisionRule.ALWAYS,
                    color != null ? color : TeamColor.RESET,
                    allowFriendlyFire != null ? allowFriendlyFire : Boolean.FALSE,
                    canSeeFriendlyInvisibles != null ? canSeeFriendlyInvisibles : Boolean.FALSE,
                    entries != null ? entries : Collections.emptyList()
            );
            eventSource.fireEvent(registerEvent);
            return new VelocityTeam(
                    scoreboard,
                    name,
                    new TeamProperties(
                            registerEvent.getDisplayName(),
                            registerEvent.getPrefix(),
                            registerEvent.getSuffix(),
                            registerEvent.getNameVisibility(),
                            registerEvent.getCollisionRule(),
                            registerEvent.getColor(),
                            registerEvent.isAllowFriendlyFire(),
                            registerEvent.isCanSeeFriendlyInvisibles()
                    ),
                    new StringCollection(registerEvent.getEntries())
            );
        }
    }
}
