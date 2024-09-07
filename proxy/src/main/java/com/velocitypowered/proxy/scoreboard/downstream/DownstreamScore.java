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
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A score that comes from a backend scoreboard.
 */
public class DownstreamScore implements Score {

    /** Score holder */
    @NotNull
    private final String holder;

    /** Score value */
    private int score;

    /** Holder's display name */
    @Nullable
    private ComponentHolder displayName;

    /** Number format for score */
    @Nullable
    private NumberFormat numberFormat;

    /**
     * Constructs new instance with given holder.
     *
     * @param   holder
     *          Score holder
     */
    public DownstreamScore(@NotNull String holder) {
        this.holder = holder;
    }

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
        this.displayName = displayName;
        this.numberFormat = numberFormat;
    }

    @Override
    @NotNull
    public String getHolder() {
        return holder;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    @Nullable
    public Component getDisplayName() {
        return displayName == null ? null : displayName.getComponent();
    }

    @Nullable
    public ComponentHolder getDisplayNameHolder() {
        return displayName;
    }

    @Override
    @Nullable
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    /**
     * Creates a dump of this score into a list of lines.
     *
     * @return  dump of this score
     */
    @NotNull
    public List<String> dump() {
        List<String> content = new ArrayList<>();
        content.add("      " + holder + ":");
        content.add("        Score: " + score);
        content.add("        DisplayName: " + displayName);
        content.add("        NumberFormat: " + numberFormat);
        return content;
    }
}
