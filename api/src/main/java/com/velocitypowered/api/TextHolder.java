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

public class TextHolder {

    /** Text holder with empty value */
    public static final TextHolder EMPTY = new TextHolder("", Component.empty());

    /** Raw text for 1.12- players */
    @Nullable
    private String legacyText;

    /** Component for 1.13+ players */
    @Nullable
    private Component modernText;

    @Nullable
    private Object componentHolder;

    public TextHolder(@NotNull String legacyText) {
        this.legacyText = legacyText;
    }

    public TextHolder(@NotNull Component modernText) {
        this.modernText = modernText;
    }

    public TextHolder(@NotNull Object componentHolder) {
        this.componentHolder = componentHolder;
    }

    public TextHolder(@NotNull String legacyText, @NotNull Component modernText) {
        this.legacyText = legacyText;
        this.modernText = modernText;
    }

    @NotNull
    public String getLegacyText() {
        if (legacyText == null) legacyText = LegacyComponentSerializer.legacySection().serialize(modernText);
        return legacyText;
    }

    @NotNull
    public String getLegacyText(int charLimit) {
        if (legacyText == null) legacyText = LegacyComponentSerializer.legacySection().serialize(modernText);
        if (legacyText.length() > charLimit) {
            return legacyText.substring(0, charLimit);
        }
        return legacyText;
    }

    @NotNull
    public Component getModernText() {
        if (modernText == null) modernText = Component.text(legacyText);
        return modernText;
    }

    @Nullable
    public Object getComponentHolder() {
        return componentHolder;
    }
}
