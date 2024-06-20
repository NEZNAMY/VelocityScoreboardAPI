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

package com.velocitypowered.proxy.scoreboard;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class DataHolder {

    private static final Map<Player, DownstreamScoreboard> DOWNSTREAM_SCOREBOARDS = Collections.synchronizedMap(new WeakHashMap<>());

    @NotNull
    public static DownstreamScoreboard getDownstreamScoreboard(@NotNull Player player) {
        return DOWNSTREAM_SCOREBOARDS.computeIfAbsent(player, DownstreamScoreboard::new);
    }
}
