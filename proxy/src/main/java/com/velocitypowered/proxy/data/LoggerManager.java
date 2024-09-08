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
import com.velocitypowered.api.scoreboard.DisplaySlot;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;

import java.util.Locale;

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
     * @param severity Severity of the invalid action
     * @param message the error message
     */
    private static void invalidDownstreamPacket(@NotNull Player player, @NotNull String severity, @NotNull String message) {
        if (!logInvalidPackets) return;
        Component component = Component.text(severity + "Received invalid downstream packet for player " + player.getUsername() + ": " + message, TextColor.fromHexString("#FF0000"));
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

    /**
     * This is a collection of invalid actions that the client silently ignores.
     */
    public static class Silent {

        public static void unknownObjectiveUnregister(@NotNull Player player, @NotNull String objectiveName) {
            invalidDownstreamPacket(player, "", "An objective with the name '" + objectiveName + "' does not exist, cannot unregister");
        }

        public static void unknownObjectiveUpdate(@NotNull Player player, @NotNull String objectiveName) {
            invalidDownstreamPacket(player, "", "An objective with the name '" + objectiveName + "' does not exist, cannot update");
        }

        public static void unknownObjectiveDisplay(@NotNull Player player, @NotNull String objectiveName, @NotNull DisplaySlot slot) {
            invalidDownstreamPacket(player, "", "An objective with the name '" + objectiveName + "' does not exist, cannot set display slot to " + slot);
        }
    }

    /**
     * This is a collection of invalid actions that result in a (yellow) warn message in the client.
     */
    public static class Warn {

        public static void doubleTeamRegister(@NotNull Player player, @NotNull String teamName) {
            // MC: Requested creation of existing team '<team>'
            invalidDownstreamPacket(player, "[WARN] ", "Team '" + teamName + "' already exists, cannot register");
        }

        public static void unknownTeamAction(@NotNull Player player, @NotNull String teamName, @NotNull TeamPacket.TeamAction action) {
            // MC: Received packet for unknown team <team>: team action: <team action>, player action: <player action>
            invalidDownstreamPacket(player, "[WARN] ", "Team '" + teamName + "' does not exist, cannot " + action.toString().toLowerCase(Locale.US));
        }

        public static void unknownObjectiveSetScore(@NotNull Player player, @NotNull String objectiveName, @NotNull String holder) {
            // MC: Received packet for unknown scoreboard objective: <objective>
            invalidDownstreamPacket(player, "[WARN] ", "Cannot set score '" + holder + "' for unknown objective '" + objectiveName + "'");
        }

        public static void unknownObjectiveResetScore(@NotNull Player player, @NotNull String objectiveName, @NotNull String holder) {
            // MC: Received packet for unknown scoreboard objective: <objective>
            invalidDownstreamPacket(player, "[WARN] ", "Cannot reset score '" + holder + "' for unknown objective '" + objectiveName + "'");
        }
    }

    /**
     * This is a collection of invalid actions that result in an error message with a stack trace in the client.
     * Since 1.20.5, it also results in the client disconnecting from the server with "Network Protocol Error".
     */
    public static class Fatal {

        public static void doubleObjectiveRegister(@NotNull Player player, @NotNull String objectiveName) {
            invalidDownstreamPacket(player, "[ERROR] ", "An objective with the name '" + objectiveName + "' already exists!"); // Taken from MC
        }

        public static void removeUnknownEntry(@NotNull Player player, @NotNull String teamName, @NotNull String entry) {
            invalidDownstreamPacket(player, "[ERROR] ","Player " + entry + " is either on another team or not on any team. " +
                    "Cannot remove from team '" + teamName + "'."); // Taken from MC (added entry name)
        }
    }
}
