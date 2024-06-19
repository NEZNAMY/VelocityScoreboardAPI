package com.velocityscoreboardapi.impl.downstream;

import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import com.velocityscoreboardapi.api.Team;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class DownstreamScoreboard {

    private final Map<String, DownstreamTeam> teams = new HashMap<>();

    public void handle(@NonNull TeamPacket packet) {
        switch (packet.getMode()) {
            case 0:
                if (teams.containsKey(packet.getName())) {
                    System.out.println("This scoreboard already contains team called " + packet.getName());
                } else {
                    teams.put(packet.getName(), DownstreamTeam.create(packet));
                }
                return;
            case 1:
                if (teams.remove(packet.getName()) == null) {
                    System.out.println("This scoreboard does not contain team called " + packet.getName() + ", cannot unregister");
                }
                return;
            case 2:
                DownstreamTeam team = teams.get(packet.getName());
                if (team == null) {
                    System.out.println("This scoreboard does not contain team called " + packet.getName() + ", cannot update");
                } else {
                    team.update(packet);
                }
                return;
            case 3:
                DownstreamTeam team2 = teams.get(packet.getName());
                if (team2 == null) {
                    System.out.println("This scoreboard does not contain team called " + packet.getName() + ", cannot add entries");
                } else {
                    team2.addEntries(packet.getEntries());
                }
                return;
            case 4:
                DownstreamTeam team3 = teams.get(packet.getName());
                if (team3 == null) {
                    System.out.println("This scoreboard does not contain team called " + packet.getName() + ", cannot remove entries");
                } else {
                    team3.removeEntries(packet.getEntries());
                }
        }
    }
}
