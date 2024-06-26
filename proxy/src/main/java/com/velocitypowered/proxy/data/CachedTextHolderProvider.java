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
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CachedTextHolderProvider extends TextHolderProvider {

//    private static final Cache<String, TextHolder> legacyCache = CacheBuilder.newBuilder()
//            .maximumSize(5000)
//            .expireAfterWrite(Duration.of(5, ChronoUnit.MINUTES))
//            .build();
//
//    private static final Cache<Component, TextHolder> modernCache = CacheBuilder.newBuilder()
//            .maximumSize(5000)
//            .expireAfterWrite(Duration.of(5, ChronoUnit.MINUTES))
//            .build();

//    private static final Cache<Pair, TextHolder> pairCache = CacheBuilder.newBuilder()
//            .maximumSize(5000)
//            .expireAfterWrite(Duration.of(5, ChronoUnit.MINUTES))
//            .build();

    private static final Map<String, TextHolder> legacyCache = new ConcurrentHashMap<>();
    private static final Map<Component, TextHolder> modernCache = new ConcurrentHashMap<>();
    private static final Map<Long, TextHolder> pairCache = new ConcurrentHashMap<>();

    public CachedTextHolderProvider(Object plugin, ProxyServer server) {
        server.getScheduler().buildTask(plugin, this::clearCache)
                .repeat(30, TimeUnit.SECONDS)
                .schedule();
    }

    public void clearCache() {
        legacyCache.clear();
        modernCache.clear();
        pairCache.clear();
    }

    @Override
    @NotNull
    public TextHolder empty() {
        return new TextHolderImpl("");
    }

    @Override
    @NotNull
    public TextHolder ofLegacy(@NotNull String legacyText) {
//        try {
//            return legacyCache.get(legacyText, () -> new TextHolderImpl(legacyText));
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
        return legacyCache.computeIfAbsent(legacyText, TextHolderImpl::new);
    }

    @Override
    @NotNull
    public TextHolder ofComponent(@NotNull Component modernText) {
//        try {
//            return modernCache.get(modernText, () -> new TextHolderImpl(modernText));
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
        return modernCache.computeIfAbsent(modernText, TextHolderImpl::new);
    }

    @Override
    @NotNull
    public TextHolder ofCombined(@NotNull String legacyText, @NotNull Component modernText) {
//        return new TextHolderImpl(legacyText, modernText);
        /*try {
            return pairCache.get(Pair.of(legacyText, modernText), () -> new TextHolderImpl(legacyText, modernText));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }*/
        /*try {
            return pairCache.get(Objects.hash(legacyText, modernText), () -> new TextHolderImpl(legacyText, modernText));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }*/
        int hash1 = legacyText.hashCode();
        int hash2 = modernText.hashCode();
        long hash = ((long) hash1 << 32) | (hash2 & 0xFFFFFFFFL);
        return pairCache.computeIfAbsent(hash, i -> new TextHolderImpl(legacyText, modernText));
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

    private record ComponentHolderWrapper(@NotNull ComponentHolder componentHolder) {

        public static ComponentHolderWrapper of(@NotNull ComponentHolder componentHolder) {
            return new ComponentHolderWrapper(componentHolder);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ComponentHolderWrapper wrapper = (ComponentHolderWrapper) obj;
            return componentHolder.equals(wrapper.componentHolder);
        }

        @Nullable
        private Component getRawComponent() {
            try {
                Field field = componentHolder.getClass().getDeclaredField("component");
                field.setAccessible(true);
                return (Component) field.get(componentHolder);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return null;
            }
        }

        @Nullable
        private String getRawLegacy() {
            try {
                Field field = componentHolder.getClass().getDeclaredField("json");
                field.setAccessible(true);
                return (String) field.get(componentHolder);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return null;
            }
        }

        @Nullable
        private BinaryTag getRawNbt() {
            try {
                Field field = componentHolder.getClass().getDeclaredField("binaryTag");
                field.setAccessible(true);
                return (BinaryTag) field.get(componentHolder);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return null;
            }
        }

        @NotNull
        private ProtocolVersion getProtocolVersion() {
            try {
                Field field = componentHolder.getClass().getDeclaredField("version");
                field.setAccessible(true);
                return (ProtocolVersion) field.get(componentHolder);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int hashCode() {
            int partialHashCode = Objects.hash(getRawComponent(), getRawLegacy(), getRawNbt());
            boolean isAfter1_16 = getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_16) >= 0;
            return 31 * partialHashCode + (isAfter1_16 ? 1 : 0);
        }

    }

}
