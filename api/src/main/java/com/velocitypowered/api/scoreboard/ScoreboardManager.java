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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Entrypoint for the Velocity Scoreboard API.
 */
public abstract class ScoreboardManager {

    /** Instance of this class set internally */
    @Nullable
    private static ScoreboardManager INSTANCE;

    /**
     * Get an instance of the Velocity Scoreboard API.
     *
     * @return a ScoreboardManager instance
     */
    @NotNull
    public static ScoreboardManager getInstance() {
        if (INSTANCE == null) throw new NotRegisteredException();
        return INSTANCE;
    }

    /**
     * Sets instance of the Scoreboard API. Internal use.
     *
     * @param   instance
     *          Scoreboard API instance
     */
    @ApiStatus.Internal
    public static void setInstance(@NotNull ScoreboardManager instance) {
        if (INSTANCE != null) throw new IllegalStateException("The VelocityScoreboardAPI has already been registered");
        INSTANCE = instance;
    }

    /**
     * Creates a new scoreboard with given parameters. Priority is used for deciding which source
     * has a higher priority when a conflict occurs. Must be greater than 0.
     * Priority 0 is reserved for downstream scoreboard.
     *
     * @param   priority
     *          Scoreboard priority
     * @param   plugin
     *          Plugin using this scoreboard
     * @return  New scoreboard
     */
    @NotNull
    public abstract Scoreboard getNewScoreboard(int priority, @NotNull Object plugin);

    /**
     * An exception indicating the plugin has been accessed before it has been registered.
     */
    static final class NotRegisteredException extends IllegalStateException {

        private static final String REASONS = """
                Could not access the VelocityScoreboardAPI as it has not yet been registered. This may be because:
                1) The VelocityScoreboardAPI plugin failed to load (are you running the latest version of Velocity?)
                2) Your plugin isn't set to depend on the "velocity-scoreboard-api" plugin (and so is loading before
                   the API has been registered)
                3) You have the API *shaded* instead of depending against it being provided (set to a "compileOnly"
                   target on Gradle, or a "provided" scope on Maven)""";

        private NotRegisteredException() {
            super(REASONS);
        }

    }

}
