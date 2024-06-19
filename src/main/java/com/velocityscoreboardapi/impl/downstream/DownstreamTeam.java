package com.velocityscoreboardapi.impl.downstream;

import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import com.velocityscoreboardapi.api.CollisionRule;
import com.velocityscoreboardapi.api.NameVisibility;
import com.velocityscoreboardapi.impl.VelocityScoreboard;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@AllArgsConstructor
public class DownstreamTeam {
    
    @NonNull private final String name;
    @NonNull private String displayNameLegacy;
    @NonNull private ComponentHolder displayNameModern;
    @NonNull private String prefixLegacy;
    @NonNull private ComponentHolder prefixModern;
    @NonNull private String suffixLegacy;
    @NonNull private ComponentHolder suffixModern;
    @NonNull private NameVisibility nameVisibility;
    @NonNull private CollisionRule collisionRule;
    private int color;
    boolean allowFriendlyFire;
    boolean canSeeFriendlyInvisibles;
    @NotNull private final Collection<String> entries;
    
    @NotNull
    public static DownstreamTeam create(@NonNull TeamPacket packet) {
        return new DownstreamTeam(
                packet.getName(),
                packet.getDisplayNameLegacy(),
                packet.getDisplayNameModern(),
                packet.getPrefixLegacy(),
                packet.getPrefixModern(),
                packet.getSuffixLegacy(),
                packet.getSuffixModern(),
                packet.getNameTagVisibility(),
                packet.getCollisionRule(),
                packet.getColor(), 
                (packet.getFlags() & 0x01) > 0, 
                (packet.getFlags() & 0x02) > 0,
                packet.getEntries()
        );
    }
    
    public void update(@NonNull TeamPacket packet) {
        displayNameLegacy = packet.getDisplayNameLegacy();
        displayNameModern = packet.getDisplayNameModern();
        prefixLegacy = packet.getPrefixLegacy();
        prefixModern = packet.getPrefixModern();
        suffixLegacy = packet.getSuffixLegacy();
        suffixModern = packet.getSuffixModern();
        nameVisibility = packet.getNameTagVisibility();
        collisionRule = packet.getCollisionRule();
        color = packet.getColor();
        allowFriendlyFire = (packet.getFlags() & 0x1) > 0;
        canSeeFriendlyInvisibles = (packet.getFlags() & 0x2) > 0;
    }

    public void addEntries(@NonNull Collection<String> entries) {
        for (String entry : new ArrayList<>(entries)) {
            if (this.entries.contains(entry)) {
                System.out.println("This team already contains entry " + entry);
                entries.remove(entry);
            }
        }
        this.entries.addAll(entries);
    }

    public void removeEntries(@NonNull Collection<String> entries) {
        for (String entry : new ArrayList<>(entries)) {
            if (!this.entries.contains(entry)) {
                System.out.println("This team does not contain entry " + entry);
                entries.remove(entry);
            }
        }
        this.entries.removeAll(entries);
    }
}
