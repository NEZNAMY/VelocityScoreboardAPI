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

package com.velocitypowered.scoreboardapi;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin's main command
 */
@RequiredArgsConstructor
public class VSACommand implements SimpleCommand {

    @NotNull
    private final ProxyServer server;

    @Override
    public void execute(@NotNull Invocation invocation) {
        CommandSource sender = invocation.source();
        if (!sender.hasPermission("vsa.dump")) {
            sender.sendMessage(Component.text("You are missing \"vsa.dump\" permission."));
            return;
        }
        String[] args = invocation.arguments();
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("dump")) {
                Player player = server.getPlayer(args[1]).orElse(null);
                if (player != null) {
                    DownstreamScoreboard scoreboard = ((DownstreamScoreboard) ScoreboardManager.getInstance().getBackendScoreboard(player));
                    try {
                        String link = scoreboard.upload();
                        TextComponent message = Component.text("See the result at " + link, TextColor.color(0x00aa00));
                        message = message.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, link));
                        sender.sendMessage(message);
                    } catch (Exception e) {
                        sender.sendMessage(Component.text("Failed to upload result, see console for more info", TextColor.color(0xff0000)));
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(Component.text("No online player found with the name \"" + args[1] + "\""));
                }
            }
        } else {
            sender.sendMessage(Component.text("Usage: /vsa dump <player>"));
        }
    }
}
