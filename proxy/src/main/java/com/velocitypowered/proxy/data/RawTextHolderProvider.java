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

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.TextHolderProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * TextHolder provider returning a new instance without any kind of caching.
 */
public class RawTextHolderProvider extends TextHolderProvider {

    @NotNull
    private final TextHolder EMPTY = new TextHolderImpl("", Component.empty());

    @Override
    @NotNull
    public TextHolder empty() {
        return EMPTY;
    }

    @Override
    @NotNull
    public TextHolder ofLegacy(@NotNull String legacyText) {
        return new TextHolderImpl(legacyText);
    }

    @Override
    @NotNull
    public TextHolder ofComponent(@NotNull Component modernText) {
        return new TextHolderImpl(modernText);
    }

    @Override
    @NotNull
    public TextHolder ofCombined(@NotNull String legacyText, @NotNull Component modernText) {
        return new TextHolderImpl(legacyText, modernText);
    }
}
