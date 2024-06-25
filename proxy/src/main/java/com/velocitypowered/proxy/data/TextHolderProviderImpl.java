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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.velocitypowered.api.TextHolder;
import com.velocitypowered.api.TextHolderProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

public class TextHolderProviderImpl extends TextHolderProvider {

    private static final Cache<String, TextHolder> legacyCache = CacheBuilder.newBuilder()
            .maximumSize(5000)
            .expireAfterWrite(Duration.of(5, ChronoUnit.MINUTES))
            .build();

    private static final Cache<Component, TextHolder> modernCache = CacheBuilder.newBuilder()
            .maximumSize(5000)
            .expireAfterWrite(Duration.of(5, ChronoUnit.MINUTES))
            .build();

    private static final Cache<Pair, TextHolder> pairCache = CacheBuilder.newBuilder()
            .maximumSize(5000)
            .expireAfterWrite(Duration.of(5, ChronoUnit.MINUTES))
            .build();

    public TextHolderProviderImpl() {
        super();
    }

    @Override
    public TextHolder empty() {
        return new TextHolderImpl("");
    }

    @Override
    public TextHolder of(@NotNull String legacyText) {
        try {
            return legacyCache.get(legacyText, () -> new TextHolderImpl(legacyText));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TextHolder of(@NotNull Component modernText) {
        try {
            return modernCache.get(modernText, () -> new TextHolderImpl(modernText));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TextHolder of(@NotNull String legacyText, @NotNull Component modernText) {
        try {
            return pairCache.get(Pair.of(legacyText, modernText), () -> new TextHolderImpl(legacyText, modernText));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private record Pair(@NotNull String legacy, @NotNull Component modern) {

        public static Pair of(@NotNull String legacy, @NotNull Component modern) {
            return new Pair(legacy, modern);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pair pair = (Pair) obj;
            return legacy.equals(pair.legacy) && modern.equals(pair.modern);
        }

        @Override
        public int hashCode() {
            return 31 * legacy.hashCode() + modern.hashCode();
        }
    }

}
