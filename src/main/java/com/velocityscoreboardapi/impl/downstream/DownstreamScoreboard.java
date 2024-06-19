package com.velocityscoreboardapi.impl.downstream;

import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import com.velocityscoreboardapi.api.DisplaySlot;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DownstreamScoreboard {

    private final Map<String, DownstreamObjective> objectives = new HashMap<>();
    private final Map<String, DownstreamTeam> teams = new HashMap<>();

    public void handle(@NonNull ObjectivePacket packet) {
        switch (packet.getAction()) {
            case 0:
                if (objectives.containsKey(packet.getObjectiveName())) {
                    System.out.println("This scoreboard already contains objective called " + packet.getObjectiveName());
                } else {
                    objectives.put(packet.getObjectiveName(), new DownstreamObjective(
                            packet.getObjectiveName(),
                            packet.getTitleLegacy(),
                            packet.getTitleModern(),
                            packet.getType(),
                            packet.getNumberFormat(),
                            null
                    ));
                }
                return;
            case 1:
                if (objectives.remove(packet.getObjectiveName()) == null) {
                    System.out.println("This scoreboard does not contain objective called " + packet.getObjectiveName() + ", cannot unregister");
                }
                return;
            case 2:
                DownstreamObjective objective = objectives.get(packet.getObjectiveName());
                if (objective == null) {
                    System.out.println("This scoreboard does not contain objective called " + packet.getObjectiveName() + ", cannot update");
                } else {
                    objective.update(packet);
                }
        }
    }

    public void handle(@NonNull DisplayObjectivePacket packet) {
        DownstreamObjective objective = objectives.get(packet.getObjectiveName());
        if (objective == null) {
            System.out.println("Cannot set display slot of unknown objective " + packet.getObjectiveName());
        } else {
            objective.setDisplaySlot(DisplaySlot.values()[packet.getPosition()]);
        }
    }

    public void handle(@NonNull ScorePacket packet) {
        if (packet.getAction() == 0) {
            if (packet.getObjectiveName() == null) return; // Invalid packet
            DownstreamObjective objective = objectives.get(packet.getObjectiveName());
            if (objective == null) {
                System.out.println("Cannot set score for unknown objective " + packet.getObjectiveName());
            } else {
                objective.setScore(packet);
            }
        } else {
            handleReset(packet.getObjectiveName(), packet.getScoreHolder());
        }
    }

    public void handle(@NonNull ScoreResetPacket packet) {
        handleReset(packet.getObjectiveName(), packet.getScoreHolder());
    }

    private void handleReset(@Nullable String objectiveName, @NonNull String holder) {
        if (objectiveName == null) {
            for (DownstreamObjective objective : objectives.values()) {
                objective.removeScore(holder);
            }
        } else {
            DownstreamObjective objective = objectives.get(objectiveName);
            if (objective == null) {
                System.out.println("Cannot reset score for unknown objective " + objectiveName);
            } else {
                objective.removeScore(holder);
            }
        }
    }

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