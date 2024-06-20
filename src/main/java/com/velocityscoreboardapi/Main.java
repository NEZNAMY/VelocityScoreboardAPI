package com.velocityscoreboardapi;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocityscoreboardapi.api.*;
import com.velocityscoreboardapi.internal.ChannelInjection;
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

    public Main() throws Exception {
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
        Scoreboard scoreboard = ScoreboardManager.getNewScoreboard(1);
        ScoreboardManager.setScoreboard(e.getPlayer(), scoreboard);
        Objective sidebar = scoreboard.registerObjective(Objective.builder().name("MyObjective").title(Component.text("§4§lTitle")).healthDisplay(HealthDisplay.INTEGER).numberFormat(NumberFormat.fixed(Component.text("-"))));
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebar.createScore(Score.builder("Line1").score(69).displayName(Component.text("Custom name for Line1")).numberFormat(NumberFormat.fixed(Component.text("NumberFormat"))));
        sidebar.createScore(Score.builder("Line2"));

        scoreboard.registerTeam(Team.builder("Team2").prefix(Component.text("prefix ")).suffix(
                Component.text(" suffix")).entries(Collections.singletonList("Line2")));
        scoreboard.registerTeam(Team.builder("PlayerTeam").prefix(Component.text("prefix ")).suffix(
                Component.text(" suffix")).entries(Collections.singletonList(e.getPlayer().getUsername())));
    }
}
