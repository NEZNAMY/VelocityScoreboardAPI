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

package com.velocitypowered.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class for holding displayable text. Minecraft 1.12 and lower uses legacy String,
 * while 1.13+ uses Components. This class holds both values and allows to set value
 * for each version interval. If initialized with only one value and the other one is
 * needed, it will be computed automatically. Manually defining both legacy and modern
 * text can be used to override automatic computation if one wants to display something else.
 */
public class TextHolder {

    /** Text holder with empty value */
    public static final TextHolder EMPTY = new TextHolder("", Component.empty());

    /** Raw text for 1.12- players */
    @Nullable
    private String legacyText;

    /** Component for 1.13+ players */
    @Nullable
    private Component modernText;

    /** Interval value used when this value was deserialized in packet decoder */
    @Nullable
    private Object componentHolder;

    /**
     * Constructs new instance with given legacy text for 1.12- players.
     * If used for 1.13+, display component will be computed automatically.
     *
     * @param   legacyText
     *          Legacy text to display
     */
    public TextHolder(@NotNull String legacyText) {
        this.legacyText = legacyText;
    }

    /**
     * Constructs new instance with given modern text for 1.13+ players.
     * If displayed on 1.12-, legacy value will be computed automatically from component
     * and cut to character limit of the feature displaying it.
     *
     * @param   modernText
     *          Modern text to display
     */
    public TextHolder(@NotNull Component modernText) {
        this.modernText = modernText;
    }

    /**
     * Constructs new instance using given component holder.
     *
     * @param   componentHolder
     *          Deserialized component holder
     * @deprecated  Internal usage
     */
    @Deprecated
    public TextHolder(@NotNull Object componentHolder) {
        this.componentHolder = componentHolder;
    }

    /**
     * Constructs new instance with given texts for both 1.12- and 1.13+.
     *
     * @param   legacyText
     *          Text to display for 1.12- players
     * @param   modernText
     *          Text to display for 1.13+ players
     */
    public TextHolder(@NotNull String legacyText, @NotNull Component modernText) {
        this.legacyText = legacyText;
        this.modernText = modernText;
    }

    /**
     * Returns legacy text for 1.12- players. If not set, it is computed and returned.
     *
     * @return  Legacy text for 1.12- players
     */
    @NotNull
    public String getLegacyText() {
        if (legacyText == null) legacyText = LegacyComponentSerializer.legacySection().serialize(modernText);
        return legacyText;
    }

    /**
     * Returns legacy text for 1.12- players. If not set, it is computed. If it exceeds the maximum
     * character limit, a substring cut down to the limit is returned.
     *
     * @param   charLimit
     *          Maximum permitted character limit
     * @return  Legacy text for 1.12- players cut down to limit
     */
    @NotNull
    public String getLegacyText(int charLimit) {
        if (legacyText == null) legacyText = LegacyComponentSerializer.legacySection().serialize(modernText);
        if (legacyText.length() > charLimit) {
            return legacyText.substring(0, charLimit);
        }
        return legacyText;
    }

    /**
     * Returns modern text for 1.13+ players. If not set, it is computed and returned.
     *
     * @return  Modern text for 1.13+ players
     */
    @NotNull
    public Component getModernText() {
        if (modernText == null) modernText = Component.text(legacyText);
        return modernText;
    }

    /**
     * Returns component holder if this value was deserialized.
     *
     * @return  Deserialized component holder
     * @deprecated  Internal usage
     */
    @Nullable
    @Deprecated
    public Object getComponentHolder() {
        return componentHolder;
    }
}
