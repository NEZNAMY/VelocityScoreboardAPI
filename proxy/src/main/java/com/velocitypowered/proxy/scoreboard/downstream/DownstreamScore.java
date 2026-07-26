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

package com.velocitypowered.proxy.scoreboard.downstream;

import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.api.scoreboard.Score;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A score that comes from a backend scoreboard.
 */
@RequiredArgsConstructor
@Getter
public class DownstreamScore implements Score {

    /** Score holder */
    @NotNull
    private final String holder;

    /** Score value */
    private int score;

    /** Holder's display name */
    @Nullable
    private ComponentHolder displayNameHolder;

    /** Number format for score */
    @Nullable
    private NumberFormat numberFormat;

    /**
     * Updates values of this score.
     *
     * @param   score
     *          Score value
     * @param   displayName
     *          Holder's display name
     * @param   numberFormat
     *          Number format for score
     */
    public void update(int score, @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        this.score = score;
        this.displayNameHolder = displayName;
        this.numberFormat = numberFormat;
    }

    @Override
    @Nullable
    public Component getDisplayName() {
        return displayNameHolder == null ? null : displayNameHolder.getComponent();
    }

    /**
     * Creates a dump of this score.
     *
     * @return  dump of this score
     */
    @NotNull
    public Map<String, Object> dump() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("Score", score);
        values.put("DisplayName", displayNameHolder);
        values.put("NumberFormat", numberFormat);
        return values;
    }
}
