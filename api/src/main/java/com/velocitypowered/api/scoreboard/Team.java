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

/**
 * An interface representing a scoreboard team.
 */
public interface Team {

    /**
     * Returns name of this team.
     *
     * @return  name of this team
     */
    @NotNull
    String getName();

    /**
     * Returns display name of this team
     *
     * @return  Display name of this team
     * @see     #setDisplayName(TextHolder)
     */
    @NotNull
    TextHolder getDisplayName();

    /**
     * Sets display name to new value.
     *
     * @param   displayName
     *          New display name to use
     * @see     #getDisplayName()
     */
    void setDisplayName(@NotNull TextHolder displayName);

    /**
     * Returns team prefix.
     *
     * @return  Team prefix
     * @see     #setPrefix(TextHolder)
     */
    @NotNull
    TextHolder getPrefix();

    /**
     * Sets team prefix to new value.
     *
     * @param   prefix
     *          New team prefix
     * @see     #getPrefix()
     */
    void setPrefix(@NotNull TextHolder prefix);

    /**
     * Returns team suffix.
     *
     * @return  Team suffix
     * @see     #setSuffix(TextHolder)
     */
    @NotNull
    TextHolder getSuffix();

    /**
     * Sets team suffix to new value.
     *
     * @param   suffix
     *          New team suffix
     * @see     #getSuffix()
     */
    void setSuffix(@NotNull TextHolder suffix);

    /**
     * Returns nametag visibility rule (1.8+).
     *
     * @return  Nametag visibility rule
     * @see      #setNameVisibility(NameVisibility)
     */
    @NotNull
    NameVisibility getNameVisibility();

    /**
     * Sets nametag visibility rule to new value (1.8+).
     *
     * @param   nameVisibility
     *          New nametag visibility rule
     * @see     #getNameVisibility()
     */
    void setNameVisibility(@NotNull NameVisibility nameVisibility);

    /**
     * Returns collision rule (1.9+).
     *
     * @return  Collision rule
     * @see     #setCollisionRule(CollisionRule)
     */
    @NotNull
    CollisionRule getCollisionRule();

    /**
     * Sets collision rule to new value (1.9+).
     *
     * @param   collisionRule
     *          New collision rule
     * @see     #getCollisionRule()
     */
    void setCollisionRule(@NotNull CollisionRule collisionRule);

    /**
     * Returns team color (1.13+).
     *
     * @return  Team color
     * @see     #getColor()
     */
    @NotNull
    TeamColor getColor();

    /**
     * Sets team color to new value (1.13+).
     *
     * @param   color
     *          New team color
     * @see     #getColor()
     */
    void setColor(@NotNull TeamColor color);

    /**
     * Returns {@code true} if team allows friendly fire between players in
     * the same team, {@code false} if not.
     *
     * @return  Friendly fire flag value
     * @see     #setAllowFriendlyFire(boolean)
     */
    boolean isAllowFriendlyFire();

    /**
     * Sets friendly fire flag to new value.
     *
     * @param   friendlyFire
     *          New friendly fire value
     * @see     #isAllowFriendlyFire()
     */
    void setAllowFriendlyFire(boolean friendlyFire);

    /**
     * Returns {@code true} if team allows seeing invisible players in the
     * same team as transparent, {@code false} if not.
     *
     * @return  Can see friendly invisibles flag value
     * @see     #setCanSeeFriendlyInvisibles(boolean)
     */
    boolean isCanSeeFriendlyInvisibles();

    /**
     * Sets can see friendly invisibles flag to new value.
     *
     * @param   canSeeFriendlyInvisibles
     *          New can see friendly invisibles value
     * @see     #isCanSeeFriendlyInvisibles()
     */
    void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

    /**
     * Updates all team properties specified in the builder. Values without
     * an assigned value will remain unchanged. Advantage of this function is that it
     * only sends a single update packet instead of a packet for every updated property.
     *
     * @param   builder
     *          Builder with changed properties
     */
    void updateProperties(@NotNull PropertyBuilder builder);

    /**
     * Returns entries currently present in this team. The returned collection is immutable,
     * use {@link #addEntry(String)} and {@link #removeEntry(String)} for adding / removing entries.
     * 
     * @return  Entries in this team
     * @see     #addEntry(String) 
     * @see     #removeEntry(String)
     */
    @NotNull
    Collection<String> getEntries();

    /**
     * Adds an entry to this team. It will be removed from all other teams.
     * For players, it is their username, for other entities their UUID.
     *
     * @param   entry
     *          Entry to add
     * @see     #getEntries()
     * @see     #removeEntry(String)
     */
    void addEntry(@NotNull String entry);

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
    void removeEntry(@NotNull String entry) throws IllegalArgumentException;

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
        PropertyBuilder displayName(@NotNull TextHolder displayName);

        /**
         * Sets prefix to specified value.
         *
         * @param   prefix
         *          Team prefix
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder prefix(@NotNull TextHolder prefix);

        /**
         * Sets suffix to specified value.
         *
         * @param   suffix
         *          Team suffix
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder suffix(@NotNull TextHolder suffix);

        /**
         * Sets nametag visibility to specified value.
         *
         * @param   visibility
         *          Nametag visibility
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder nameVisibility(@NotNull NameVisibility visibility);

        /**
         * Sets collision rule to specified value.
         *
         * @param   collisionRule
         *          Collision rule
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder collisionRule(@NotNull CollisionRule collisionRule);

        /**
         * Sets color to specified value.
         *
         * @param   color
         *          Team color
         * @return  this, for chaining
         */
        @NotNull
        PropertyBuilder color(@NotNull TeamColor color);

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
        Builder displayName(@NotNull TextHolder displayName);

        /**
         * Sets prefix to specified value.
         *
         * @param   prefix
         *          Team prefix
         * @return  this, for chaining
         */
        @NotNull
        Builder prefix(@NotNull TextHolder prefix);

        /**
         * Sets suffix to specified value.
         *
         * @param   suffix
         *          Team suffix
         * @return  this, for chaining
         */
        @NotNull
        Builder suffix(@NotNull TextHolder suffix);

        /**
         * Sets nametag visibility to specified value.
         *
         * @param   visibility
         *          Nametag visibility
         * @return  this, for chaining
         */
        @NotNull
        Builder nameVisibility(@NotNull NameVisibility visibility);

        /**
         * Sets collision rule to specified value.
         *
         * @param   collisionRule
         *          Collision rule
         * @return  this, for chaining
         */
        @NotNull
        Builder collisionRule(@NotNull CollisionRule collisionRule);

        /**
         * Sets color to specified value.
         *
         * @param   color
         *          Team color
         * @return  this, for chaining
         */
        @NotNull
        Builder color(@NotNull TeamColor color);

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
         * Adds specified entries into the team.
         *
         * @param   entries
         *          Entries to add
         * @return  this, for chaining
         */
        @NotNull
        Builder entries(@NotNull Collection<String> entries);

        /**
         * Adds specified entries into the team.
         *
         * @param   entries
         *          Entries to add
         * @return  this, for chaining
         */
        @NotNull
        default Builder entries(@NotNull String... entries) {
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
        default Builder entries(@NotNull Player... players) {
            return entries(Arrays.stream(players).map(Player::getUsername).toList());
        }
    }
}
