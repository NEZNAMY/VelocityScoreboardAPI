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

package com.velocitypowered.proxy.protocol.packet.downstream;

import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.scoreboard.ScorePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DownstreamScore {

    @NotNull private final String holder;
    private int score;
    @Nullable private ComponentHolder displayName;
    @Nullable private NumberFormat numberFormat;

    public DownstreamScore(@NotNull String holder, int score, @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        this.holder = holder;
        this.score = score;
        this.displayName = displayName;
        this.numberFormat = numberFormat;
    }

    public void update(@NotNull ScorePacket packet) {
        score = packet.getValue();
        displayName = packet.getDisplayName();
        numberFormat = packet.getNumberFormat();
    }
}
