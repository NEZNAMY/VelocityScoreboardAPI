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

package com.velocitypowered.proxy.scoreboard.downstream;

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.CollisionRule;
import com.velocitypowered.api.scoreboard.NameVisibility;
import com.velocitypowered.api.scoreboard.Team;
import com.velocitypowered.api.scoreboard.TeamColor;
import com.velocitypowered.proxy.data.LoggerManager;
import com.velocitypowered.proxy.data.StringCollection;
import com.velocitypowered.proxy.scoreboard.TeamProperties;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A scoreboard team that comes from the backend.
 */
public class DownstreamTeam implements Team {

    /**
     * Team name
     */
    @NotNull
    private final String name;

    /**
     * Team properties
     */
    @NotNull
    private TeamProperties properties;

    /**
     * Entries in the team
     */
    @NotNull
    private final StringCollection entries;

    /**
     * Constructs new instance with given parameters.
     *
     * @param name       Team name
     * @param properties Team properties
     * @param entries    Entries in the team
     */
    public DownstreamTeam(@NotNull String name, @NotNull TeamProperties properties, @NotNull StringCollection entries) {
        this.name = name;
        this.properties = properties;
        this.entries = entries;
    }

    /**
     * Updates team properties.
     *
     * @param properties New team properties
     */
    public void setProperties(@NotNull TeamProperties properties) {
        this.properties = properties;
    }

    /**
     * Adds entries to the team.
     *
     * @param entries Entries to add
     */
    public void addEntries(@NotNull StringCollection entries) {
        this.entries.addAll(entries);
    }

    /**
     * Removes entries from team. If they are not present, prints a warning.
     *
     * @param viewer Player who received the packet
     * @param entries Entries to remove
     */
    public void removeEntries(@NotNull Player viewer, @NotNull StringCollection entries) {
        if (entries.getEntry() != null) {
            if (!this.entries.contains(entries.getEntry())) {
                LoggerManager.Fatal.removeUnknownEntry(viewer, name, entries.getEntry());
                entries.remove(entries.getEntry());
            }
        } else {
            List<String> toRemove = null;
            for (String entry : entries.getEntries()) {
                if (!this.entries.contains(entry)) {
                    LoggerManager.Fatal.removeUnknownEntry(viewer, name, entry);
                    if (toRemove == null) toRemove = new ArrayList<>();
                    toRemove.add(entry);
                }
            }
            if (toRemove != null) {
                for (String entryToRemove : toRemove) {
                    entries.remove(entryToRemove);
                }
            }
        }
        this.entries.removeAll(entries);
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

    /**
     * Retrieves the properties of the team.
     *
     * @return The properties of the team
     */
    @NotNull
    public TeamProperties getProperties() {
        return properties;
    }

    @Override
    @NotNull
    public Collection<String> getEntries() {
        return entries.getEntries();
    }

    @NotNull
    public StringCollection getEntryCollection() {
        return entries;
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
}
