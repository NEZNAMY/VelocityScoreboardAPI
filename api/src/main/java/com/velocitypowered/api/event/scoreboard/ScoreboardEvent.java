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

import com.velocitypowered.api.proxy.Player;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract event class for all scoreboard-related events.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ScoreboardEvent {

    /** Player who received the scoreboard change */
    @NotNull
    private final Player player;

    /** {@code true} if this event is mutable (proxy scoreboard), {@code false} if not (backend scoreboard) */
    private final boolean mutable;

    /**
     * Checks if the event is mutable and if not, throws an {@link UnsupportedOperationException}.
     *
     * @throws  UnsupportedOperationException
     *          if the event is not mutable
     */
    protected void ensureMutable() {
        if (!mutable) {
            throw new UnsupportedOperationException("This event affects the backend scoreboard and is not mutable.");
        }
    }
}
