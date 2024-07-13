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
import org.jetbrains.annotations.NotNull;

/**
 * Class for holding displayable text. Minecraft 1.12 and lower uses legacy String,
 * while 1.13+ uses Components. This class holds both values and allows to set value
 * for each version interval. If initialized with only one value and the other one is
 * needed, it will be computed automatically. Manually defining both legacy and modern
 * text can be used to override automatic computation if one wants to display something else.
 */
public abstract class TextHolder {

    /** Text holder with empty value */
    public static final TextHolder EMPTY = TextHolderProvider.getProvider().empty();

    /**
     * Constructs a TextHolder object with the given legacy text
     * or returns a cached instance if previously constructed with the same text.
     *
     * @param legacyText The legacy text to be used in the TextHolder
     * @return The constructed TextHolder object
     */
    public static TextHolder of(@NotNull String legacyText) {
        return TextHolderProvider.getProvider().ofLegacy(legacyText);
    }

    /**
     * Constructs a TextHolder object with the given Component modern text
     * or returns a cached instance if previously constructed with the same text.
     *
     * @param modernText The Component modern text to be used in the TextHolder
     * @return The constructed TextHolder object
     */
    public static TextHolder of(@NotNull Component modernText) {
        return TextHolderProvider.getProvider().ofComponent(modernText);
    }

    /**
     * Constructs a TextHolder object with both legacy and modern texts
     * or returns a cached instance if previously constructed with the same texts.
     *
     * @param legacyText Text for 1.12- players
     * @param modernText Text for 1.13+ players
     * @return The constructed TextHolder object
     */
    public static TextHolder of(@NotNull String legacyText, @NotNull Component modernText) {
        return TextHolderProvider.getProvider().ofCombined(legacyText, modernText);
    }

    /**
     * Returns legacy text for 1.12- players. If not set, it is computed and returned.
     *
     * @return  Legacy text for 1.12- players
     */
    @NotNull
    public abstract String getLegacyText();

    /**
     * Returns legacy text for 1.12- players. If not set, it is computed. If it exceeds the maximum
     * character limit, a substring cut down to the limit is returned.
     *
     * @param   charLimit
     *          Maximum permitted character limit
     * @return  Legacy text for 1.12- players cut down to limit
     */
    @NotNull
    public abstract String getLegacyText(int charLimit);

    /**
     * Returns modern text for 1.13+ players. If not set, it is computed and returned.
     *
     * @return  Modern text for 1.13+ players
     */
    @NotNull
    public abstract Component getModernText();
}
