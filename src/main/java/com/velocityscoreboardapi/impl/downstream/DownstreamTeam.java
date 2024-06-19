package com.velocityscoreboardapi.impl.downstream;

import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import com.velocityscoreboardapi.api.CollisionRule;
import com.velocityscoreboardapi.api.NameVisibility;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
public class DownstreamTeam {
    
    @NotNull private final String name;
    @Nullable private String displayNameLegacy;
    @Nullable private ComponentHolder displayNameModern;
    @Nullable private String prefixLegacy;
    @Nullable private ComponentHolder prefixModern;
    @Nullable private String suffixLegacy;
    @Nullable private ComponentHolder suffixModern;
    @NotNull private NameVisibility nameVisibility;
    @NotNull private CollisionRule collisionRule;
    private int color;
    boolean allowFriendlyFire;
    boolean canSeeFriendlyInvisibles;
    @NotNull private final Collection<String> entries;
    
    @NotNull
    public static DownstreamTeam create(@NotNull TeamPacket packet) {
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
    
    public void update(@NotNull TeamPacket packet) {
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

    public void addEntries(@NotNull Collection<String> entries) {
        for (String entry : new ArrayList<>(entries)) {
            if (this.entries.contains(entry)) {
                System.out.println("This team already contains entry " + entry);
                entries.remove(entry);
            }
        }
        this.entries.addAll(entries);
    }

    public void removeEntries(@NotNull Collection<String> entries) {
        for (String entry : new ArrayList<>(entries)) {
            if (!this.entries.contains(entry)) {
                System.out.println("This team does not contain entry " + entry);
                entries.remove(entry);
            }
        }
        this.entries.removeAll(entries);
    }
}
