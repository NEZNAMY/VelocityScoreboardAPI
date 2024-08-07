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

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;

/**
 * This class managers logging of various problems that may occur.
 */
public class LoggerManager {

    /**
     * The logger instance.
     */
    private final static ComponentLogger logger = ComponentLogger.logger("VelocityScoreboardAPI");

    /**
     * Whether to log invalid packets.
     */
    private static boolean logInvalidPackets;

    /**
     * Sets whether to log invalid packets.
     * @param logInvalidPackets whether to log invalid packets
     */
    public static void setLogInvalidPackets(boolean logInvalidPackets) {
        LoggerManager.logInvalidPackets = logInvalidPackets;
    }

    /**
     * Logs an error message indicating an invalid downstream packet.
     *
     * @param player Player who received invalid packet
     * @param message the error message
     */
    public static void invalidDownstreamPacket(@NotNull Player player, @NotNull String message) {
        if (!logInvalidPackets) return;
        Component component = Component.text("Received invalid downstream packet for player " + player.getUsername() + ": " + message, TextColor.fromHexString("#FF0000"));
        log(Level.ERROR, component);
    }

    /**
     * Logs a message with the specified log level.
     *
     * @param level      the log level
     * @param message    the log message (MiniMessage format)
     * @param exceptions the exceptions associated with the log message (optional)
     */
    public static void log(@NotNull Level level, @NotNull String message, @NotNull Throwable... exceptions) {
        log(level, MiniMessage.miniMessage().deserialize(message), exceptions);
    }

    /**
     * Logs a message with the specified log level.
     *
     * @param level      the log level
     * @param message    the log message
     * @param exceptions the exceptions associated with the log message (optional)
     */
    public static void log(@NotNull Level level, @NotNull Component message, @NotNull Throwable... exceptions) {
        switch (level) {
            case ERROR -> {
                if (exceptions.length > 0) {
                    logger.error(message, exceptions[0]);
                } else {
                    logger.error(message);
                }
            }
            case WARN -> {
                if (exceptions.length > 0) {
                    logger.warn(message, exceptions[0]);
                } else {
                    logger.warn(message);
                }
            }
            case DEBUG -> logger.debug(message);
            case INFO -> logger.info(message);
        }
    }
}
