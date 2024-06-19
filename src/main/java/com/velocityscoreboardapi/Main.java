package com.velocityscoreboardapi;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.Either;
import com.velocitypowered.proxy.protocol.NumberFormat;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.DisplayObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ObjectivePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScorePacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;

@Plugin(
        id = "velocityscoreboardapi",
        name = "VelocityScoreboardAPI",
        version = "1.0.0",
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
    public void onSwitch(ServerPostConnectEvent e) {
        System.out.println("Post connect");
        ((ConnectedPlayer)e.getPlayer()).getConnection().write(new ObjectivePacket(false,
                "Objective",
                Either.right(new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text("§4§lTitle"))),
                ObjectivePacket.HealthDisplay.INTEGER,
                (byte) 0,
                new NumberFormat(NumberFormat.Type.FIXED, new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text("-")))
        ));
        ((ConnectedPlayer)e.getPlayer()).getConnection().write(new DisplayObjectivePacket(false,1, "Objective"));
        ((ConnectedPlayer)e.getPlayer()).getConnection().write(new ScorePacket(false,
                "Line1",
                (byte) 0,
                "Objective",
                69,
                new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text("Custom name for Line1")),
                new NumberFormat(NumberFormat.Type.FIXED, new ComponentHolder(e.getPlayer().getProtocolVersion(), Component.text("NumberFormat")))
        ));
        ((ConnectedPlayer)e.getPlayer()).getConnection().write(new ScorePacket(false,
                "Line2",
                (byte) 0,
                "Objective",
                69,
                null,
                null
        ));
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
