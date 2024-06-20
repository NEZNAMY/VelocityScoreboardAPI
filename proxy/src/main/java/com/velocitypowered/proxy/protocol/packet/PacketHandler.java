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

package com.velocitypowered.proxy.protocol.packet;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.connection.client.ClientPlaySessionHandler;
import com.velocitypowered.proxy.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class PacketHandler {

    private static final Field playerField;

    static {
        try {
            playerField = ClientPlaySessionHandler.class.getDeclaredField("player");
            playerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Player getPlayer(@NotNull MinecraftSessionHandler handler) {
        try {
            return (Player) playerField.get(handler);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull DisplayObjectivePacket packet) {
        DataHolder.getDownstreamScoreboard(handler).handle(packet);
        return false;
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ObjectivePacket packet) {
        DataHolder.getDownstreamScoreboard(handler).handle(packet);
        return false;
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ScorePacket packet) {
        DataHolder.getDownstreamScoreboard(handler).handle(packet);
        return false;
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ScoreResetPacket packet) {
        DataHolder.getDownstreamScoreboard(handler).handle(packet);
        return false;
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull TeamPacket packet) {
        DataHolder.getDownstreamScoreboard(handler).handle(packet);
        return false;
    }
}
