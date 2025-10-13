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
import com.velocitypowered.proxy.scoreboard.VelocityScoreboard;
import com.velocitypowered.proxy.scoreboard.downstream.DownstreamScoreboard;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("dump")) {
                Player player = server.getPlayer(args[2]).orElse(null);
                if (player == null) {
                    sender.sendMessage(Component.text("No online player found with the name \"" + args[2] + "\""));
                    return;
                }
                try {
                    String link;
                    if (args[1].equalsIgnoreCase("proxy")) {
                        VelocityScoreboard scoreboard = ((VelocityScoreboard) ScoreboardManager.getInstance().getProxyScoreboard(player));
                        link = upload(scoreboard.dump());
                    } else if (args[1].equalsIgnoreCase("backend")) {
                        DownstreamScoreboard scoreboard = ((DownstreamScoreboard) ScoreboardManager.getInstance().getBackendScoreboard(player));
                        link = upload(scoreboard.dump());
                    } else {
                        sender.sendMessage(Component.text("Usage: /vsa dump <proxy/backend> <player> (unknown type \"" + args[1] + "\")"));
                        return;
                    }
                    TextComponent message = Component.text("See the result at " + link, TextColor.color(0x00aa00));
                    message = message.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, link));
                    sender.sendMessage(message);
                } catch (Exception e) {
                    sender.sendMessage(Component.text("Failed to upload result, see console for more info", TextColor.color(0xff0000)));
                    e.printStackTrace();
                }
            }
        } else {
            sender.sendMessage(Component.text("Usage: /vsa dump <proxy/backend> <player>"));
        }
    }

    @NotNull
    private String upload(@NotNull List<String> dump) throws Exception {
        String contentString = String.join("\n", dump) + "\n";

        URL url = new URL("https://api.pastes.dev/post");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/log; charset=UTF-8");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(contentString.getBytes(StandardCharsets.UTF_8));
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();

        String responseString = response.toString();
        String id = responseString.substring(responseString.indexOf("\"key\":\"") + 7, responseString.indexOf("\"", responseString.indexOf("\"key\":\"") + 7));

        return "https://pastes.dev/" + id;
    }
}
