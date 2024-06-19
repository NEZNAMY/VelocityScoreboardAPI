package com.velocityscoreboardapi;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocityscoreboardapi.api.*;
import com.velocityscoreboardapi.internal.ChannelInjection;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;

import java.util.Collections;

@Plugin(
        id = "velocityscoreboardapi",
        name = "VelocityScoreboardAPI",
        version = "0.0.1",
        description = "Adds Scoreboard API to Velocity",
        url = "-",
        authors = "NEZNAMY"
)
public class Main {

    @SneakyThrows
    public Main() {
        try {
            if (ProtocolVersion.MAXIMUM_VERSION != ProtocolVersion.MINECRAFT_1_21) {
                throw new IllegalStateException("Your Velocity build is too new for this plugin version. This plugin version only supports up to 1.21" +
                        " (Your velocity build supports " + ProtocolVersion.MAXIMUM_VERSION + ").");
            }
        } catch (NoSuchFieldError e) {
            throw new IllegalStateException("The plugin requires a newer velocity build that supports MC 1.21.");
        }
        PacketRegistry.registerPackets();
        System.out.println("[VelocityScoreboardAPI] Successfully injected Scoreboard API.");
    }

    @Subscribe
    public void onJoin(PostLoginEvent e) {
        ((ConnectedPlayer)e.getPlayer()).getConnection().getChannel().pipeline().addBefore("handler", "VelocityPacketAPI", new ChannelInjection(e.getPlayer()));
    }

    @Subscribe
    @SuppressWarnings("UnstableApiUsage")
    public void onSwitch(ServerPostConnectEvent e) {
        System.out.println(e.getClass().getName());
        System.out.println(e.getClass().getName());
        Scoreboard scoreboard = ScoreboardManager.getNewScoreboard(0);
        ScoreboardManager.setScoreboard(e.getPlayer(), scoreboard);
        Objective sidebar = scoreboard.registerNewObjective("MyObjective", Component.text("§4§lTitle"), HealthDisplay.INTEGER, NumberFormat.fixed(Component.text("-")));
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebar.findOrCreateScore("Line1", 69, Component.text("Custom name for Line1"), NumberFormat.fixed(Component.text("NumberFormat")));
        sidebar.findOrCreateScore("Line2");

        scoreboard.registerNewTeam("Team2", Component.text("Team2"), Component.text("prefix "),
                Component.text(" suffix"), NameVisibility.ALWAYS, CollisionRule.ALWAYS, 0, false,
                false, Collections.singletonList("Line2"));

        scoreboard.registerNewTeam("PlayerTeam", Component.text("Display"), Component.text("prefix "),
                Component.text(" suffix"), NameVisibility.ALWAYS, CollisionRule.ALWAYS, 0, false,
                false, Collections.singletonList(e.getPlayer().getUsername()));
    }
}
