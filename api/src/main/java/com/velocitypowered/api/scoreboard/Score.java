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
import org.jetbrains.annotations.Nullable;

public interface Score {

//    @NotNull
//    static Builder builder(@NotNull String holder) {
//        return new VelocityScore.Builder(holder);
//    }

    @NotNull
    String getHolder();

    int getScore();

    void setScore(int score);

    @Nullable
    Component getDisplayName();

    void setDisplayName(@Nullable Component displayName);

    @Nullable
    NumberFormat getNumberFormat();

    void setNumberFormat(@Nullable NumberFormat numberFormat);

    interface Builder {

        @NotNull
        Builder holder(@NotNull String holder);

        @NotNull
        Builder score(int score);

        @NotNull
        Builder displayName(@Nullable Component displayName);

        @NotNull
        Builder numberFormat(@Nullable NumberFormat numberFormat);

        @NotNull
        Score build(@NotNull Objective objective);

    }

}
