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

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.scoreboard.ScoreboardEventSource;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scoreboard.ScoreboardManager;
import com.velocitypowered.proxy.data.LoggerManager;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboard;
import com.velocitypowered.proxy.scoreboard.VelocityScoreboardManager;
import lombok.Getter;
import lombok.NonNull;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.event.Level;

import java.nio.file.Path;

/**
 * Entrypoint for Velocity Scoreboard API.
 */
public class VelocityScoreboardAPI implements ScoreboardEventSource {

    @Getter
    private final ProxyServer server;
    private final Metrics.Factory metricsFactory;
    private final PluginConfig pluginConfig;

    /**
     * Constructs new instance with given parameters.
     *
     * @param   server
     *          Proxy server
     * @param   metricsFactory
     *          Metrics for bStats
     * @param   configDirectory
     *          Configuration directory
     */
    @Inject
    public VelocityScoreboardAPI(@NotNull ProxyServer server, @NotNull Metrics.Factory metricsFactory,
                                 @DataDirectory Path configDirectory) {
        this.server = server;
        this.metricsFactory = metricsFactory;
        this.pluginConfig = PluginConfig.load(configDirectory);

        LoggerManager.setLogInvalidPackets(pluginConfig.isPrintInvalidDownstreamPacketWarnings());

        CommandManager cmd = server.getCommandManager();
        cmd.register(cmd.metaBuilder("vsa").build(), new VSACommand(server));
    }

    /**
     * Method called when the proxy server is being initialized.
     * It checks if the Velocity build version is compatible with the plugin and performs necessary setup steps.
     *
     * @param event The proxy initialization event
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            if (ProtocolVersion.MAXIMUM_VERSION != VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION) {
                LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(80));
                LoggerManager.log(Level.ERROR,"<red>Your Velocity build supports MC version " + ProtocolVersion.MAXIMUM_VERSION +
                        ", but this plugin only supports up to " + VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION + ".");
                LoggerManager.log(Level.ERROR,"<red>Plugin will be disabled for players with unsupported versions to avoid risk.");
                LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(80));
            }
        } catch (NoSuchFieldError e) {
            LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(80));
            LoggerManager.log(Level.ERROR,"<red>The plugin requires velocity build #453 and up to work.");
            LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(80));
            return;
        }

        try {
            PacketRegistry.registerPackets(VelocityScoreboard.MAXIMUM_SUPPORTED_VERSION);
        } catch (Throwable e) {
            LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(80));
            Throwable cause = e.getCause();
            if (cause != null && cause.getMessage().contains("because another packet is already registered")) {
                LoggerManager.log(Level.ERROR, "Failed to register scoreboard packets. " +
                        "This most likely means you are using another plugin that already registered scoreboard packets. Error message:");
                LoggerManager.log(Level.ERROR, cause.getClass().getName() + ": " + cause.getMessage());
            } else {
                LoggerManager.log(Level.ERROR,"<red>An error occurred while registering packets. This most likely means Velocity had a breaking change " +
                        "and this plugin needs an update.");
                e.printStackTrace();
            }
            LoggerManager.log(Level.ERROR,"<red>" + "-".repeat(80));
            return;
        }

        ScoreboardManager.setInstance(new VelocityScoreboardManager(server, this));
        LoggerManager.log(Level.INFO,"<green>Successfully injected Scoreboard API.");
        server.getEventManager().register(this, new EventListener(this));
        metricsFactory.make(this, 22437);
    }

    @Override
    public void fireEvent(@NonNull Object event) {
        if (!pluginConfig.isCallScoreboardEvents()) return;
        server.getEventManager().fire(event);
    }

}
