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

import com.velocitypowered.proxy.data.LoggerManager;
import com.velocitypowered.proxy.scoreboard.TeamProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A scoreboard team that comes from the backend.
 */
public class DownstreamTeam {

    /** Team name */
    @NotNull
    private final String name;

    /** Team properties */
    @NotNull
    private TeamProperties properties;

    /** Entries in the team */
    @NotNull
    private final Collection<String> entries;

    /**
     * Constructs new instance with given parameters.
     *
     * @param   name
     *          Team name
     * @param   properties
     *          Team properties
     * @param   entries
     *          Entries in the team
     */
    public DownstreamTeam(@NotNull String name, @NotNull TeamProperties properties, @NotNull Collection<String> entries) {
        this.name = name;
        this.properties = properties;
        this.entries = entries;
    }

    /**
     * Updates team properties.
     *
     * @param   properties
     *          New team properties
     */
    public void setProperties(@NotNull TeamProperties properties) {
        this.properties = properties;
    }

    /**
     * Adds entries to the team.
     *
     * @param   entries
     *          Entries to add
     */
    public void addEntries(@NotNull Collection<String> entries) {
        this.entries.addAll(entries);
    }

    /**
     * Removes entries from team. If they are not present, prints a warning.
     *
     * @param   entries
     *          Entries to remove
     */
    public void removeEntries(@NotNull Collection<String> entries) {
        for (String entry : entries) {
            if (!this.entries.contains(entry)) {
                LoggerManager.invalidDownstreamPacket("Team " + name + " does not contain entry " + entry);
            }
        }
        this.entries.removeAll(entries);
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

    /**
     * Removes entries from team.
     *
     * @param   entries
     *          Entries to remove
     */
    public void removeEntriesIfPresent(@NotNull Collection<String> entries) {
        this.entries.removeAll(entries);
    }
}
