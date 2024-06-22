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

import com.velocitypowered.proxy.data.PacketLogger;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import com.velocitypowered.proxy.scoreboard.TeamProperties;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DownstreamTeam {
    
    @NotNull private final String name;
    @NotNull private TeamProperties properties;
    @NotNull private final Collection<String> entries;

    public DownstreamTeam(@NotNull String name, @NotNull TeamProperties properties, @NotNull Collection<String> entries) {
        this.name = name;
        this.properties = properties;
        this.entries = entries;
    }

    public void update(@NotNull TeamPacket packet) {
        properties = packet.getProperties();
    }

    public void addEntries(@NotNull Collection<String> entries) {
        this.entries.addAll(entries);
    }

    public void removeEntries(@NotNull Collection<String> entries) {
        for (String entry : entries) {
            if (!this.entries.contains(entry)) {
                PacketLogger.invalidDownstreamPacket("Team " + name + " does not contain entry " + entry);
            }
        }
        this.entries.removeAll(entries);
    }

    public void removeEntriesIfPresent(@NotNull Collection<String> entries) {
        this.entries.removeAll(entries);
    }
}
