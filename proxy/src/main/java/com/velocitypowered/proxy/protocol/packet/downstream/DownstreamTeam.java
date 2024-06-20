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

package com.velocitypowered.proxy.protocol.packet.downstream;

import com.velocitypowered.api.scoreboard.CollisionRule;
import com.velocitypowered.api.scoreboard.NameVisibility;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class DownstreamTeam {
    
    @NotNull private final String name;
    @Nullable private String displayNameLegacy;
    @Nullable private ComponentHolder displayNameModern;
    @Nullable private String prefixLegacy;
    @Nullable private ComponentHolder prefixModern;
    @Nullable private String suffixLegacy;
    @Nullable private ComponentHolder suffixModern;
    @NotNull private NameVisibility nameVisibility;
    @NotNull private CollisionRule collisionRule;
    private int color;
    boolean allowFriendlyFire;
    boolean canSeeFriendlyInvisibles;
    @NotNull private final Collection<String> entries;

    public DownstreamTeam(@NotNull String name, @Nullable String displayNameLegacy, @Nullable ComponentHolder displayNameModern,
                          @Nullable String prefixLegacy, @Nullable ComponentHolder prefixModern, @Nullable String suffixLegacy,
                          @Nullable ComponentHolder suffixModern, @NotNull NameVisibility nameVisibility, @NotNull CollisionRule collisionRule,
                          int color, boolean allowFriendlyFire, boolean canSeeFriendlyInvisibles, @NotNull Collection<String> entries) {
        this.name = name;
        this.displayNameLegacy = displayNameLegacy;
        this.displayNameModern = displayNameModern;
        this.prefixLegacy = prefixLegacy;
        this.prefixModern = prefixModern;
        this.suffixLegacy = suffixLegacy;
        this.suffixModern = suffixModern;
        this.nameVisibility = nameVisibility;
        this.collisionRule = collisionRule;
        this.color = color;
        this.allowFriendlyFire = allowFriendlyFire;
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        this.entries = entries;
    }

    @NotNull
    public static DownstreamTeam create(@NotNull TeamPacket packet) {
        return new DownstreamTeam(
                packet.getName(),
                packet.getDisplayNameLegacy(),
                packet.getDisplayNameModern(),
                packet.getPrefixLegacy(),
                packet.getPrefixModern(),
                packet.getSuffixLegacy(),
                packet.getSuffixModern(),
                packet.getNameTagVisibility(),
                packet.getCollisionRule(),
                packet.getColor(), 
                (packet.getFlags() & 0x01) > 0, 
                (packet.getFlags() & 0x02) > 0,
                packet.getEntries()
        );
    }
    
    public void update(@NotNull TeamPacket packet) {
        displayNameLegacy = packet.getDisplayNameLegacy();
        displayNameModern = packet.getDisplayNameModern();
        prefixLegacy = packet.getPrefixLegacy();
        prefixModern = packet.getPrefixModern();
        suffixLegacy = packet.getSuffixLegacy();
        suffixModern = packet.getSuffixModern();
        nameVisibility = packet.getNameTagVisibility();
        collisionRule = packet.getCollisionRule();
        color = packet.getColor();
        allowFriendlyFire = (packet.getFlags() & 0x1) > 0;
        canSeeFriendlyInvisibles = (packet.getFlags() & 0x2) > 0;
    }

    public void addEntries(@NotNull Collection<String> entries) {
        for (String entry : new ArrayList<>(entries)) {
            if (this.entries.contains(entry)) {
                System.out.println("This team already contains entry " + entry);
                entries.remove(entry);
            }
        }
        this.entries.addAll(entries);
    }

    public void removeEntries(@NotNull Collection<String> entries) {
        for (String entry : new ArrayList<>(entries)) {
            if (!this.entries.contains(entry)) {
                System.out.println("This team does not contain entry " + entry);
                entries.remove(entry);
            }
        }
        this.entries.removeAll(entries);
    }
}
