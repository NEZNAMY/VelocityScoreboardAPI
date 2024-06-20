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

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Team {

//    @NotNull
//    static Team.Builder builder(@NotNull String name) {
//        return new VelocityTeam.Builder(name);
//    }

    @NotNull
    String getName();

    @NotNull
    Component getDisplayName();

    void setDisplayName(@NotNull Component displayName);

    @NotNull
    Component getPrefix();

    void setPrefix(@NotNull Component prefix);

    @NotNull
    Component getSuffix();

    void setSuffix(@NotNull Component suffix);

    @NotNull
    NameVisibility getNameVisibility();

    void setNameVisibility(@NotNull NameVisibility visibility);

    @NotNull
    CollisionRule getCollisionRule();

    void setCollisionRule(@NotNull CollisionRule collisionRule);

    int getColor();

    void setColor(int color);

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
        Builder name(@NotNull String name);

        @NotNull
        Builder displayName(@NotNull Component displayName);

        @NotNull
        Builder prefix(@NotNull Component prefix);

        @NotNull
        Builder suffix(@NotNull Component suffix);

        @NotNull
        Builder nameVisibility(@NotNull NameVisibility visibility);

        @NotNull
        Builder collisionRule(@NotNull CollisionRule collisionRule);

        @NotNull
        Builder color(int color);

        @NotNull
        Builder allowFriendlyFire(boolean friendlyFire);

        @NotNull
        Builder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

        @NotNull
        Builder entries(@NotNull Collection<String> entries);

        @NotNull
        Team build(@NotNull Scoreboard scoreboard);

    }

}
