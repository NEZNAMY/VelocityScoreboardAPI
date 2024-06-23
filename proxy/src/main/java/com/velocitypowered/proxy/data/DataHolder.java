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

package com.velocitypowered.proxy.data;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataHolder {

    private static final Map<UUID, ScoreboardManager> SCOREBOARD_MANAGERS = new ConcurrentHashMap<>();

    @NotNull
    public static ScoreboardManager getScoreboardManager(@NotNull Player player) {
        return SCOREBOARD_MANAGERS.computeIfAbsent(player.getUniqueId(), uuid -> new ScoreboardManager(player));
    }

    public static void removeScoreboardManager(@NotNull Player player) {
        Optional.ofNullable(SCOREBOARD_MANAGERS.remove(player.getUniqueId()))
                .ifPresent(ScoreboardManager::handleDisconnect);
    }
}
