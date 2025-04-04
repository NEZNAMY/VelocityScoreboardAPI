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
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An interface representing a proxy scoreboard team.
 */
public interface ProxyTeam extends Team {

    /**
     * Sets display name to new value.
     *
     * @param   displayName
     *          New display name to use
     * @see     #getDisplayName()
     */
    void setDisplayName(@NonNull TextHolder displayName);

    /**
     * Sets team prefix to new value.
     *
     * @param   prefix
     *          New team prefix
     * @see     #getPrefix()
     */
    void setPrefix(@NonNull TextHolder prefix);

    /**
     * Sets team suffix to new value.
     *
     * @param   suffix
     *          New team suffix
     * @see     #getSuffix()
     */
    void setSuffix(@NonNull TextHolder suffix);

    /**
     * Sets nametag visibility rule to new value (1.8+).
     *
     * @param   nameVisibility
     *          New nametag visibility rule
     * @see     #getNameVisibility()
     */
    void setNameVisibility(@NonNull NameVisibility nameVisibility);

    /**
     * Sets collision rule to new value (1.9+).
     *
     * @param   collisionRule
     *          New collision rule
     * @see     #getCollisionRule()
     */
    void setCollisionRule(@NonNull CollisionRule collisionRule);

    /**
     * Sets team color to new value (1.13+).
     *
     * @param   color
     *          New team color
     * @see     #getColor()
     */
    void setColor(@NonNull TeamColor color);

    /**
     * Sets friendly fire flag to new value.
     *
     * @param   friendlyFire
     *          New friendly fire value
     * @see     #isAllowFriendlyFire()
     */
    void setAllowFriendlyFire(boolean friendlyFire);

    /**
     * Sets can see friendly invisibles flag to new value.
     *
     * @param   canSeeFriendlyInvisibles
     *          New can see friendly invisibles value
     * @see     #isCanSeeFriendlyInvisibles()
     */
    void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

    /**
     * Updates all team properties specified in the builder consumer.
     * Values without an assigned value will remain unchanged.
     * Advantage of this function is that it only sends a single update packet
     * instead of a packet for every updated property.
     *
     * @param   builderConsumer
     *          Consumer with changed properties
     */
    void updateProperties(@NonNull Consumer<PropertyBuilder> builderConsumer);

    /**
     * Adds an entry to this team. It will be removed from all other teams.
     * For players, it is their username, for other entities their UUID.
     *
     * @param   entry
     *          Entry to add
     * @see     #getEntries()
     * @see     #removeEntry(String)
     */
    void addEntry(@NonNull String entry);

    /**
     * Removes entry from this team. If entry is not present, throws
     * {@link IllegalArgumentException}.
     *
     * @param   entry
     *          Entry to remove
     * @see     #addEntry(String)
     * @see     #getEntries()
     * @throws  IllegalArgumentException
     *          If given entry is not in this team
     */
    void removeEntry(@NonNull String entry) throws IllegalArgumentException;

    /**
     * This is a builder for team properties.
     */
    interface PropertyBuilder {

        /**
         * Sets display name to specified value.
         *
         * @param   displayName
         *          Team display name
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder displayName(@NonNull TextHolder displayName);

        /**
         * Sets prefix to specified value.
         *
         * @param   prefix
         *          Team prefix
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder prefix(@NonNull TextHolder prefix);

        /**
         * Sets suffix to specified value.
         *
         * @param   suffix
         *          Team suffix
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder suffix(@NonNull TextHolder suffix);

        /**
         * Sets nametag visibility to specified value.
         *
         * @param   visibility
         *          Nametag visibility
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder nameVisibility(@NonNull NameVisibility visibility);

        /**
         * Sets collision rule to specified value.
         *
         * @param   collisionRule
         *          Collision rule
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder collisionRule(@NonNull CollisionRule collisionRule);

        /**
         * Sets color to specified value.
         *
         * @param   color
         *          Team color
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder color(@NonNull TeamColor color);

        /**
         * Sets friendly fire to specified value.
         *
         * @param   friendlyFire
         *          Friendly fire
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder allowFriendlyFire(boolean friendlyFire);

        /**
         * Sets can see friendly invisibles to specified value.
         *
         * @param   canSeeFriendlyInvisibles
         *          Can see friendly invisibles
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);
    }

    /**
     * Team builder interface.
     */
    interface Builder {

        /**
         * Sets display name to specified value.
         *
         * @param   displayName
         *          Team display name
         * @return  this, for chaining
         */
        @NotNull
        Builder displayName(@NonNull TextHolder displayName);

        /**
         * Sets prefix to specified value.
         *
         * @param   prefix
         *          Team prefix
         * @return  this, for chaining
         */
        @NotNull
        Builder prefix(@NonNull TextHolder prefix);

        /**
         * Sets suffix to specified value.
         *
         * @param   suffix
         *          Team suffix
         * @return  this, for chaining
         */
        @NotNull
        Builder suffix(@NonNull TextHolder suffix);

        /**
         * Sets nametag visibility to specified value.
         *
         * @param   visibility
         *          Nametag visibility
         * @return  this, for chaining
         */
        @NotNull
        Builder nameVisibility(@NonNull NameVisibility visibility);

        /**
         * Sets collision rule to specified value.
         *
         * @param   collisionRule
         *          Collision rule
         * @return  this, for chaining
         */
        @NotNull
        Builder collisionRule(@NonNull CollisionRule collisionRule);

        /**
         * Sets color to specified value.
         *
         * @param   color
         *          Team color
         * @return  this, for chaining
         */
        @NotNull
        Builder color(@NonNull TeamColor color);

        /**
         * Sets friendly fire to specified value.
         *
         * @param   friendlyFire
         *          Friendly fire
         * @return  this, for chaining
         */
        @NotNull
        Builder allowFriendlyFire(boolean friendlyFire);

        /**
         * Sets can see friendly invisibles to specified value.
         *
         * @param   canSeeFriendlyInvisibles
         *          Can see friendly invisibles
         * @return  this, for chaining
         */
        @NotNull
        Builder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

        /**
         * Adds specified entry into the team.
         *
         * @param   entry
         *          Entry to add
         * @return  this, for chaining
         */
        @NotNull
        Builder entry(@NonNull String entry);

        /**
         * Adds specified entries into the team.
         *
         * @param   entries
         *          Entries to add
         * @return  this, for chaining
         */
        @NotNull
        Builder entries(@NonNull Collection<String> entries);

        /**
         * Adds specified entries into the team.
         *
         * @param   entries
         *          Entries to add
         * @return  this, for chaining
         */
        @NotNull
        default Builder entries(@NonNull String... entries) {
            return entries(Set.of(entries));
        }

        /**
         * Adds specified players into the team.
         *
         * @param   players
         *          Players to add
         * @return  this, for chaining
         */
        @NotNull
        default Builder entries(@NonNull Player... players) {
            return entries(Arrays.stream(players).map(Player::getUsername).toList());
        }
    }
}
