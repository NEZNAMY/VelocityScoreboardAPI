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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * A scoreboard with proxy objectives and proxy teams.
 */
public interface ProxyScoreboard extends Scoreboard {

    /**
     * Gets proxy objective by name. If no such objective is registered, returns {@code null}.
     *
     * @param   name
     *          Objective name
     * @return  Objective by given name or {@code null} if not present
     * @see     #createObjective(String, Consumer)
     */
    @Nullable
    ProxyObjective getObjective(@NotNull String name);

    /**
     * Retrieves the proxy objective displayed in the specified display slot.
     *
     * @param displaySlot The display slot where the objective is shown
     * @return The proxy objective displayed in the specified display slot, or {@code null} if no objective is present
     */
    @Nullable
    ProxyObjective getObjective(@NotNull DisplaySlot displaySlot);

    /**
     * Retrieves the set of objectives in the scoreboard.
     *
     * @return a set of objectives in the scoreboard
     */
    @NotNull
    Collection<ProxyObjective> getObjectives();

    /**
     * Creates a new objective builder.
     *
     * @param   name
     *          Objective name
     * @return  Objective builder with given objective name
     */
    @NotNull
    ProxyObjective.Builder objectiveBuilder(@NotNull String name);

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
    ProxyObjective registerObjective(@NotNull ProxyObjective.Builder builder) throws IllegalStateException;

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
    default ProxyObjective createObjective(@NotNull String name, @NotNull Consumer<ProxyObjective.Builder> consumer) {
        ProxyObjective.Builder builder = objectiveBuilder(name);
        consumer.accept(builder);
        return registerObjective(builder);
    }

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
     * Returns team with give name. If no such team is present, returns {@code null}.
     *
     * @param   teamName
     *          Team name
     * @return  Team with given name, {@code null} if no such team exists
     * @see     #createTeam(String, Consumer)
     */
    @Nullable
    ProxyTeam getTeam(@NotNull String teamName);

    /**
     * Retrieves the set of teams in the scoreboard.
     *
     * @return a set of teams in the scoreboard
     */
    @NotNull
    Collection<ProxyTeam> getTeams();

    /**
     * Creates a new team builder.
     *
     * @param   name
     *          Team name
     * @return  Team builder with given team name
     */
    @NotNull
    ProxyTeam.Builder teamBuilder(@NotNull String name);

    /**
     * Creates a new team property builder.
     *
     * @return  New team property builder
     */
    @NotNull
    ProxyTeam.PropertyBuilder teamPropertyBuilder();

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
    ProxyTeam registerTeam(@NotNull ProxyTeam.Builder builder) throws IllegalStateException;

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
    default ProxyTeam createTeam(@NotNull String name, @NotNull Consumer<ProxyTeam.Builder> consumer) throws IllegalStateException {
        ProxyTeam.Builder builder = teamBuilder(name);
        consumer.accept(builder);
        return registerTeam(builder);
    }

    /**
     * Unregisters team with given name.
     *
     * @param   teamName
     *          Name of team to unregister
     * @throws  IllegalStateException
     *          If no such team exists
     */
    void unregisterTeam(@NotNull String teamName) throws IllegalStateException;
}
