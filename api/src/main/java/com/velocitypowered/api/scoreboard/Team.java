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

package com.velocitypowered.api.scoreboard;

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public interface Team {

    @NotNull
    String getName();

    @NotNull
    TextHolder getDisplayName();

    void setDisplayName(@NotNull TextHolder displayName);

    @NotNull
    TextHolder getPrefix();

    void setPrefix(@NotNull TextHolder prefix);

    @NotNull
    TextHolder getSuffix();

    void setSuffix(@NotNull TextHolder suffix);

    @NotNull
    NameVisibility getNameVisibility();

    void setNameVisibility(@NotNull NameVisibility visibility);

    @NotNull
    CollisionRule getCollisionRule();

    void setCollisionRule(@NotNull CollisionRule collisionRule);

    @NotNull
    TeamColor getColor();

    void setColor(@NotNull TeamColor color);

    boolean isAllowFriendlyFire();

    void setAllowFriendlyFire(boolean friendlyFire);

    boolean isCanSeeFriendlyInvisibles();

    void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

    @NotNull
    Collection<String> getEntries();

    void addEntry(@NotNull String entry);

    void removeEntry(@NotNull String entry);

    interface Builder {

        @NotNull
        Builder displayName(@NotNull TextHolder displayName);

        @NotNull
        Builder prefix(@NotNull TextHolder prefix);

        @NotNull
        Builder suffix(@NotNull TextHolder suffix);

        @NotNull
        Builder nameVisibility(@NotNull NameVisibility visibility);

        @NotNull
        Builder collisionRule(@NotNull CollisionRule collisionRule);

        @NotNull
        Builder color(@NotNull TeamColor color);

        @NotNull
        Builder allowFriendlyFire(boolean friendlyFire);

        @NotNull
        Builder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

        @NotNull
        Builder entries(@NotNull Collection<String> entries);

        @NotNull
        default Builder entries(@NotNull String... entries) {
            return entries(Set.of(entries));
        }

        @NotNull
        default Builder entries(@NotNull Player... player) {
            return entries(Arrays.stream(player).map(Player::getUsername).toList());
        }

        @NotNull
        Team build(@NotNull Scoreboard scoreboard);

    }

}
