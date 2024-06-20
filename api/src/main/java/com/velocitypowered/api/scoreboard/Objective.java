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

import java.util.function.Consumer;

public interface Objective {

    @Nullable
    DisplaySlot getDisplaySlot();

    void setDisplaySlot(@NotNull DisplaySlot displaySlot);

    @NotNull
    String getName();

    @NotNull
    Component getTitle();

    void setTitle(@NotNull Component title);

    @NotNull
    HealthDisplay getHealthDisplay();

    void setHealthDisplay(@NotNull HealthDisplay healthDisplay);

    @Nullable
    NumberFormat getNumberFormat();

    void setNumberFormat(@NotNull NumberFormat numberFormat);

    @NotNull
    Score.Builder scoreBuilder(@NotNull String holder);

    @NotNull
    Score registerScore(@NotNull Score.Builder builder);

    default void createScore(@NotNull String holder, @NotNull Consumer<Score.Builder> consumer) {
        Score.Builder builder = scoreBuilder(holder);
        consumer.accept(builder);
        registerScore(builder);
    }

    @NotNull
    Score getScore(@NotNull String name);

    void removeScore(@NotNull String name);

    void removeScore(@NotNull Score score);

    interface Builder extends NumberFormat.Builder {

        @NotNull
        Builder title(@NotNull Component title);

        @NotNull
        Builder healthDisplay(@NotNull HealthDisplay healthDisplay);

        @NotNull
        Objective build(@NotNull Scoreboard scoreboard);

    }
}
