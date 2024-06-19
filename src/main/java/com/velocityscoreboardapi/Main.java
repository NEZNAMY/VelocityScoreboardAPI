package com.velocityscoreboardapi;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.Either;
import com.velocityscoreboardapi.api.*;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;

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
            if (ProtocolVersion.MAXIMUM_VERSION != ProtocolVersion.MINECRAFT_1_20_5) {
                throw new IllegalStateException("The plugin requires a newer velocity build that supports MC 1.20.5.");
            }
        } catch (NoSuchFieldError e) {
            throw new IllegalStateException("Your Velocity build is too new for this plugin version. This plugin version only supports up to 1.20.5.");
        }
        PacketRegistry.registerPackets();
        System.out.println("[VelocityScoreboardAPI] Successfully injected Scoreboard API.");
    }

    @Subscribe
    @SuppressWarnings("UnstableApiUsage")
    public void onSwitch(ServerPostConnectEvent e) {
        Scoreboard scoreboard = ScoreboardManager.getNewScoreboard();
        ScoreboardManager.setScoreboard(e.getPlayer(), scoreboard);
        Objective sidebar = scoreboard.registerNewObjective("MyObjective", Component.text("§4§lTitle"), HealthDisplay.INTEGER, NumberFormat.fixed(Component.text("-")));
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sidebar.findOrCreateScore("Line1", 69, Component.text("Custom name for Line1"), NumberFormat.fixed(Component.text("NumberFormat")));
        sidebar.findOrCreateScore("Line2");

        ((ConnectedPlayer)e.getPlayer()).getConnection().write(new TeamPacket(false,
                "Team2",
                (byte) 0,
                Either.right(new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text("Display"))),
                Either.right(new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text("prefix "))),
                Either.right(new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text(" suffix"))),
                "always",
                "always",
                0,
                (byte) 0,
                new String[]{"Line2"}
        ));



        ((ConnectedPlayer)e.getPlayer()).getConnection().write(new TeamPacket(false,
                "PlayerTeam",
                (byte) 0,
                Either.right(new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text("Display"))),
                Either.right(new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text("prefix "))),
                Either.right(new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text(" suffix"))),
                "always",
                "always",
                0,
                (byte) 0,
                new String[]{e.getPlayer().getUsername()}
        ));
    }
}
