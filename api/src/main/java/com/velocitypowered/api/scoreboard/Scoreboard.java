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

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface Scoreboard {

    void addPlayer(@NotNull Player player);

    @NotNull
    Objective.Builder objectiveBuilder(@NotNull String name);

    @NotNull
    Objective registerObjective(@NotNull Objective.Builder builder);

    default Objective createObjective(@NotNull String name, @NotNull Consumer<Objective.Builder> consumer) {
        Objective.Builder builder = objectiveBuilder(name);
        consumer.accept(builder);
        return registerObjective(builder);
    }

    @Nullable
    Objective getObjective(@NotNull String name);

    void unregisterObjective(@NotNull String objectiveName);

    @NotNull
    Team.Builder teamBuilder(@NotNull String name);

    @NotNull
    Team registerTeam(@NotNull Team.Builder builder);

    default void createTeam(@NotNull String name, @NotNull Consumer<Team.Builder> consumer) {
        Team.Builder builder = teamBuilder(name);
        consumer.accept(builder);
        registerTeam(builder);
    }

    @Nullable
    Team getTeam(@NotNull String teamName);

    void unregisterTeam(@NotNull String teamName);

}
