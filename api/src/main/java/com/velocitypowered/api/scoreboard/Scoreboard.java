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

/**
 * A scoreboard with objectives and teams.
 */
public interface Scoreboard {

    /**
     * Adds a player into this scoreboard.
     * 
     * @param   player
     *          Player to add
     * @see     #removePlayer(Player) 
     */
    void addPlayer(@NotNull Player player);

    /**
     * Removes player from this scoreboard.
     * 
     * @param   player
     *          Player to remove
     * @see     #addPlayer(Player)
     */
    void removePlayer(@NotNull Player player);

    /**
     * Creates a new objective builder.
     *
     * @param   name
     *          Objective name
     * @return  Objective builder with given objective name
     */
    @NotNull
    Objective.Builder objectiveBuilder(@NotNull String name);

    /**
     * Registers objective into this scoreboard.
     *
     * @param   builder
     *          Objective builder
     * @return  Registered objective
     * @throws  IllegalStateException
     *          If objective with this name already exists
     */
    @NotNull
    Objective registerObjective(@NotNull Objective.Builder builder) throws IllegalStateException;

    /**
     * Registers objective into this scoreboard.
     *
     * @param   name
     *          Objective name
     * @param   consumer
     *          Objective parameters
     * @return  Registered objective
     */
    @NotNull
    default Objective createObjective(@NotNull String name, @NotNull Consumer<Objective.Builder> consumer) {
        Objective.Builder builder = objectiveBuilder(name);
        consumer.accept(builder);
        return registerObjective(builder);
    }

    /**
     * Gets objective by name. If no such objective is registered, returns {@code null}.
     *
     * @param   name
     *          Objective name
     * @return  Objective by given name or {@code null} if not present
     */
    @Nullable
    Objective getObjective(@NotNull String name);

    /**
     * Unregisters objective by name.
     *
     * @param   objectiveName
     *          Name of objective to unregister
     * @throws  IllegalStateException
     *          If no such objective is registered
     */
    void unregisterObjective(@NotNull String objectiveName) throws IllegalStateException;

    /**
     * Creates a new team builder.
     *
     * @param   name
     *          Team name
     * @return  Team builder with given team name
     */
    @NotNull
    Team.Builder teamBuilder(@NotNull String name);

    /**
     * Creates a new team property builder.
     *
     * @return  New team property builder
     */
    @NotNull
    Team.PropertyBuilder teamPropertyBuilder();

    /**
     * Registers team into this scoreboard.
     *
     * @param   builder
     *          Team builder
     * @return  Registered team
     * @throws  IllegalStateException
     *          If team with this name already exists
     */
    @NotNull
    Team registerTeam(@NotNull Team.Builder builder) throws IllegalStateException;

    /**
     * Registers team into this scoreboard.
     *
     * @param   name
     *          Team name
     * @param   consumer
     *          Team builder
     * @return  Registered team
     * @throws  IllegalStateException
     *          If team with this name already exists
     */
    @NotNull
    default Team createTeam(@NotNull String name, @NotNull Consumer<Team.Builder> consumer) throws IllegalStateException {
        Team.Builder builder = teamBuilder(name);
        consumer.accept(builder);
        return registerTeam(builder);
    }

    /**
     * Returns team with give name. If no such team is present, returns {@code null}.
     *
     * @param   teamName
     *          Team name
     * @return  Team with given name, {@code null} if no such team exists
     */
    @Nullable
    Team getTeam(@NotNull String teamName);

    /**
     * Unregisters team with given name.
     *
     * @param   teamName
     *          Name of team to unregister
     * @throws  IllegalStateException
     *          If no such team exists
     */
    void unregisterTeam(@NotNull String teamName) throws IllegalStateException;

    /**
     * Returns plugin that created this scoreboard.
     *
     * @return  plugin that created this scoreboard
     */
    @NotNull
    Object holder();
}
