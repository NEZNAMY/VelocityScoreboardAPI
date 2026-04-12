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

package com.velocitypowered.api.event.scoreboard;

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scoreboard.CollisionRule;
import com.velocitypowered.api.scoreboard.NameVisibility;
import com.velocitypowered.api.scoreboard.TeamColor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;

/**
 * Abstract class for team-related events.
 */
@Getter
public abstract class TeamEvent extends ScoreboardEvent {

    /** Name of the affected team */
    @NonNull
    private final String teamName;

    /**
     * Constructs new instance with given parameters.
     *
     * @param   player
     *          Player who received the scoreboard change
     * @param   mutable
     *          Whether this objective is mutable (proxy) or not (backend)
     * @param   teamName
     *          Name of affected team
     */
    public TeamEvent(@NonNull Player player, boolean mutable, @NonNull String teamName) {
        super(player, mutable);
        this.teamName = teamName;
    }

    /**
     * This event is called when a team is registered.
     */
    @Getter
    public static class Register extends TeamEvent {

        /** Display name of the team */
        @NonNull
        private TextHolder displayName;

        /** Team prefix */
        @NonNull
        private TextHolder prefix;

        /** Team suffix */
        @NonNull
        private TextHolder suffix;

        /** Nametag visibility for 1.8+ */
        @NonNull
        private NameVisibility nameVisibility;

        /** Collision rule for 1.9+ */
        @NonNull
        private CollisionRule collisionRule;

        /** Team color for 1.13+ */
        @NonNull
        private TeamColor color;

        /** Friendly fire between players in the same team */
        private boolean allowFriendlyFire;

        /** Seeing player in the same team as transparent when invisible */
        private boolean canSeeFriendlyInvisibles;

        /** Entries in the team*/
        @NonNull
        private Collection<String> entries;

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   mutable
         *          Whether this objective is mutable (proxy) or not (backend)
         * @param   teamName
         *          Name of affected team
         * @param   displayName
         *          Display name of the team
         * @param   prefix
         *          Team prefix
         * @param   suffix
         *          Team suffix
         * @param   nameVisibility
         *          Nametag visibility for 1.8+
         * @param   collisionRule
         *          Collision rule for 1.9+
         * @param   color
         *          Team color for 1.13+
         * @param   allowFriendlyFire
         *          Friendly fire between players in the same team
         * @param   canSeeFriendlyInvisibles
         *          Seeing player in the same team as transparent when invisible
         * @param   entries
         *          Entries in the team
         */
        public Register(@NonNull Player player, boolean mutable, @NonNull String teamName,
                        @NonNull TextHolder displayName, @NonNull TextHolder prefix,
                        @NonNull TextHolder suffix, @NonNull NameVisibility nameVisibility,
                        @NonNull CollisionRule collisionRule, @NonNull TeamColor color,
                        boolean allowFriendlyFire, boolean canSeeFriendlyInvisibles,
                        @NonNull Collection<String> entries) {
            super(player, mutable, teamName);
            this.displayName = displayName;
            this.prefix = prefix;
            this.suffix = suffix;
            this.nameVisibility = nameVisibility;
            this.collisionRule = collisionRule;
            this.color = color;
            this.allowFriendlyFire = allowFriendlyFire;
            this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
            this.entries = entries;
        }

        /**
         * Sets the display name to new value.
         *
         * @param   displayName
         *          New display name
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setDisplayName(@NonNull TextHolder displayName) {
            ensureMutable();
            this.displayName = displayName;
        }

        /**
         * Sets the prefix to new value.
         *
         * @param   prefix
         *          New prefix
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setPrefix(@NonNull TextHolder prefix) {
            ensureMutable();
            this.prefix = prefix;
        }

        /**
         * Sets the suffix to new value.
         *
         * @param   suffix
         *          New suffix
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setSuffix(@NonNull TextHolder suffix) {
            ensureMutable();
            this.suffix = suffix;
        }

        /**
         * Sets the name visibility to new value.
         *
         * @param   nameVisibility
         *          New name visibility
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setNameVisibility(@NonNull NameVisibility nameVisibility) {
            ensureMutable();
            this.nameVisibility = nameVisibility;
        }

        /**
         * Sets the collision rule to new value.
         *
         * @param   collisionRule
         *          New collision rule
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setCollisionRule(@NonNull CollisionRule collisionRule) {
            ensureMutable();
            this.collisionRule = collisionRule;
        }

        /**
         * Sets the color to new value.
         *
         * @param   color
         *          New color
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setColor(@NonNull TeamColor color) {
            ensureMutable();
            this.color = color;
        }

        /**
         * Sets the allow friendly fire flag to new value.
         *
         * @param   allowFriendlyFire
         *          New flag value
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setAllowFriendlyFire(boolean allowFriendlyFire) {
            ensureMutable();
            this.allowFriendlyFire = allowFriendlyFire;
        }

        /**
         * Sets the can see friendly invisibles flag to new value.
         *
         * @param   canSeeFriendlyInvisibles
         *          New flag value
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
            ensureMutable();
            this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        }

        /**
         * Replaces entry collection with a new one.
         *
         * @param   entries
         *          New entry collection
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setEntries(@NonNull Collection<String> entries) {
            ensureMutable();
            this.entries = entries;
        }
    }

    /**
     * This event is called when a team is unregistered.
     */
    public static class Unregister extends TeamEvent {

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   mutable
         *          Whether this objective is mutable (proxy) or not (backend)
         * @param   teamName
         *          Name of affected team
         */
        public Unregister(@NonNull Player player, boolean mutable, @NonNull String teamName) {
            super(player, mutable, teamName);
        }
    }

    /**
     * This event is called when a team is updated.
     */
    @Getter
    public static class Update extends TeamEvent {

        /** Display name of the team */
        @NonNull
        private TextHolder displayName;

        /** Team prefix */
        @NonNull
        private TextHolder prefix;

        /** Team suffix */
        @NonNull
        private TextHolder suffix;

        /** Nametag visibility for 1.8+ */
        @NonNull
        private NameVisibility nameVisibility;

        /** Collision rule for 1.9+ */
        @NonNull
        private CollisionRule collisionRule;

        /** Team color for 1.13+ */
        @NonNull
        private TeamColor color;

        /** Friendly fire between players in the same team */
        private boolean allowFriendlyFire;

        /** Seeing player in the same team as transparent when invisible */
        private boolean canSeeFriendlyInvisibles;

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   mutable
         *          Whether this objective is mutable (proxy) or not (backend)
         * @param   teamName
         *          Name of affected team
         * @param   displayName
         *          Display name of the team
         * @param   prefix
         *          Team prefix
         * @param   suffix
         *          Team suffix
         * @param   nameVisibility
         *          Nametag visibility for 1.8+
         * @param   collisionRule
         *          Collision rule for 1.9+
         * @param   color
         *          Team color for 1.13+
         * @param   allowFriendlyFire
         *          Friendly fire between players in the same team
         * @param   canSeeFriendlyInvisibles
         *          Seeing player in the same team as transparent when invisible
         */
        public Update(@NonNull Player player, boolean mutable, @NonNull String teamName,
                        @NonNull TextHolder displayName, @NonNull TextHolder prefix,
                        @NonNull TextHolder suffix, @NonNull NameVisibility nameVisibility,
                        @NonNull CollisionRule collisionRule, @NonNull TeamColor color,
                        boolean allowFriendlyFire, boolean canSeeFriendlyInvisibles) {
            super(player, mutable, teamName);
            this.displayName = displayName;
            this.prefix = prefix;
            this.suffix = suffix;
            this.nameVisibility = nameVisibility;
            this.collisionRule = collisionRule;
            this.color = color;
            this.allowFriendlyFire = allowFriendlyFire;
            this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        }

        /**
         * Sets the display name to new value.
         *
         * @param   displayName
         *          New display name
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setDisplayName(@NonNull TextHolder displayName) {
            ensureMutable();
            this.displayName = displayName;
        }

        /**
         * Sets the prefix to new value.
         *
         * @param   prefix
         *          New prefix
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setPrefix(@NonNull TextHolder prefix) {
            ensureMutable();
            this.prefix = prefix;
        }

        /**
         * Sets the suffix to new value.
         *
         * @param   suffix
         *          New suffix
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setSuffix(@NonNull TextHolder suffix) {
            ensureMutable();
            this.suffix = suffix;
        }

        /**
         * Sets the name visibility to new value.
         *
         * @param   nameVisibility
         *          New name visibility
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setNameVisibility(@NonNull NameVisibility nameVisibility) {
            ensureMutable();
            this.nameVisibility = nameVisibility;
        }

        /**
         * Sets the collision rule to new value.
         *
         * @param   collisionRule
         *          New collision rule
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setCollisionRule(@NonNull CollisionRule collisionRule) {
            ensureMutable();
            this.collisionRule = collisionRule;
        }

        /**
         * Sets the color to new value.
         *
         * @param   color
         *          New color
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setColor(@NonNull TeamColor color) {
            ensureMutable();
            this.color = color;
        }

        /**
         * Sets the allow friendly fire flag to new value.
         *
         * @param   allowFriendlyFire
         *          New flag value
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setAllowFriendlyFire(boolean allowFriendlyFire) {
            ensureMutable();
            this.allowFriendlyFire = allowFriendlyFire;
        }

        /**
         * Sets the can see friendly invisibles flag to new value.
         *
         * @param   canSeeFriendlyInvisibles
         *          New flag value
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
            ensureMutable();
            this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        }
    }

    /**
     * This event is fired when entries are added to a team.
     */
    @Getter
    public static class AddPlayers extends TeamEvent {

        /** Affected entries */
        @NonNull
        private Collection<String> entries;

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   mutable
         *          Whether this objective is mutable (proxy) or not (backend)
         * @param   teamName
         *          Team where entry was added
         * @param   entries
         *          Affected entries
         */
        public AddPlayers(@NonNull Player player, boolean mutable, @NonNull String teamName, @NonNull Collection<String> entries) {
            super(player, mutable, teamName);
            this.entries = entries;
        }

        /**
         * Replaces entry collection with a new one.
         *
         * @param   entries
         *          New entry collection
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setEntries(@NonNull Collection<String> entries) {
            ensureMutable();
            this.entries = entries;
        }
    }

    /**
     * This event is fired when entries are removed from a team.
     */
    @Getter
    public static class RemovePlayers extends TeamEvent {

        /** Affected entries */
        @NonNull
        private Collection<String> entries;

        /**
         * Constructs new instance with given parameters.
         *
         * @param   player
         *          Player who received the scoreboard change
         * @param   mutable
         *          Whether this objective is mutable (proxy) or not (backend)
         * @param   teamName
         *          Team where entry was added
         * @param   entries
         *          Affected entries
         */
        public RemovePlayers(@NonNull Player player, boolean mutable, @NonNull String teamName, @NonNull Collection<String> entries) {
            super(player, mutable, teamName);
            this.entries = entries;
        }

        /**
         * Replaces entry collection with a new one.
         *
         * @param   entries
         *          New entry collection
         * @throws  UnsupportedOperationException
         *          If this event is not mutable (it affects backend scoreboard)
         */
        public void setEntries(@NonNull Collection<String> entries) {
            ensureMutable();
            this.entries = entries;
        }
    }
}
