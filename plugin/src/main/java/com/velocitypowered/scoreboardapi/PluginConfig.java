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

import de.exlll.configlib.*;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@SuppressWarnings("FieldCanBeLocal")
@Configuration
public class PluginConfig {

    @NotNull
    private static final String FILE_HEADER = """
            ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
            ┃     VelocityScoreboardAPI    ┃
            ┃          Config File         ┃
            ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
            ┃ This file is used to configure the VelocityScoreboardAPI plugin.
            ┃ This plugin is intended to be used with Velocity player list plugins such as TAB or Velocitab,
            ┃ and should be installed on your Velocity proxy server alongside one of those plugins.
            ┗╸ GitHub: https://github.com/NEZNAMY/VelocityScoreboardAPI/""";

    @NotNull
    private static final YamlConfigurationProperties.Builder<?> YAML_CONFIG = YamlConfigurationProperties.newBuilder()
            .charset(StandardCharsets.UTF_8)
            .setNameFormatter(NameFormatters.LOWER_UNDERSCORE);

    @Comment("Whether scoreboard API events should be fired")
    private boolean callScoreboardEvents = true;

    @Comment("Whether to log invalid packets received from downstream servers")
    private boolean printInvalidDownstreamPacketWarnings = true;

    private PluginConfig() {
    }

    @NotNull
    public static PluginConfig load(@NotNull Path directory) {
        return YamlConfigurations.update(
                directory.resolve("config.yml"),
                PluginConfig.class,
                YAML_CONFIG.header(PluginConfig.FILE_HEADER).build()
        );
    }

    public boolean isCallScoreboardEvents() {
        return callScoreboardEvents;
    }

    public boolean isPrintInvalidDownstreamPacketWarnings() {
        return printInvalidDownstreamPacketWarnings;
    }

}
