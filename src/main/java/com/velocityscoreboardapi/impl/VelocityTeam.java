package com.velocityscoreboardapi.impl;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import com.velocityscoreboardapi.api.CollisionRule;
import com.velocityscoreboardapi.api.NameVisibility;
import com.velocityscoreboardapi.api.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

@Getter
@AllArgsConstructor
public class VelocityTeam implements Team {

    @NonNull private final VelocityScoreboard scoreboard;
    @NonNull private final String name;
    @NonNull private Component displayName;
    @NonNull private Component prefix;
    @NonNull private Component suffix;
    @NonNull private NameVisibility nameVisibility;
    @NonNull private CollisionRule collisionRule;
    private int color; // Cannot use NamedTextColor because it does not have ordinals + does not support magic codes or even reset
    boolean allowFriendlyFire;
    boolean canSeeFriendlyInvisibles;
    @NotNull private final Collection<String> entries;
    private boolean registered;

    @Override
    public void setDisplayName(@NonNull Component displayName) {
        checkState();
        this.displayName = displayName;
        sendUpdate();
    }

    @Override
    public void setPrefix(@NonNull Component prefix) {
        checkState();
        this.prefix = prefix;
        sendUpdate();
    }

    @Override
    public void setSuffix(@NonNull Component suffix) {
        checkState();
        this.suffix = suffix;
        sendUpdate();
    }

    @Override
    public void setNameVisibility(@NonNull NameVisibility visibility) {
        checkState();
        this.nameVisibility = visibility;
        sendUpdate();
    }

    @Override
    public void setCollisionRule(@NonNull CollisionRule collisionRule) {
        checkState();
        this.collisionRule = collisionRule;
        sendUpdate();
    }

    @Override
    public void setColor(int color) {
        checkState();
        this.color = color;
        sendUpdate();
    }

    @Override
    public void setAllowFriendlyFire(boolean friendlyFire) {
        checkState();
        this.allowFriendlyFire = friendlyFire;
        sendUpdate();
    }

    @Override
    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        checkState();
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
        sendUpdate();
    }

    @Override
    public void addEntry(@NonNull String entry) {
        checkState();
        if (entries.add(entry)) {
            sendModifyEntry(entry, true);
        } else {
            throw new IllegalArgumentException("This entry is already in the team");
        }
    }

    @Override
    public void removeEntry(@NonNull String entry) {
        checkState();
        if (entries.remove(entry)) {
            sendModifyEntry(entry, false);
        } else {
            throw new IllegalArgumentException("This entry is not in the team");
        }
    }

    public void sendRegister() {
        String legacyDisplayName = LegacyComponentSerializer.legacySection().serialize(displayName);
        if (legacyDisplayName.length() > 16) legacyDisplayName = legacyDisplayName.substring(0, 16);
        String legacyPrefix = LegacyComponentSerializer.legacySection().serialize(prefix);
        if (legacyPrefix.length() > 16) legacyPrefix = legacyPrefix.substring(0, 16);
        String legacySuffix = LegacyComponentSerializer.legacySection().serialize(suffix);
        if (legacySuffix.length() > 16) legacySuffix = legacySuffix.substring(0, 16);
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new TeamPacket(scoreboard.getPriority(), name, (byte) 0, legacyDisplayName,
                    new ComponentHolder(player.getProtocolVersion(), displayName), legacyPrefix,
                    new ComponentHolder(player.getProtocolVersion(), prefix), legacySuffix,
                    new ComponentHolder(player.getProtocolVersion(), suffix), nameVisibility, collisionRule, color, getFlags(), entries.toArray(new String[0])));
        }
    }

    private void sendUpdate() {
        String legacyDisplayName = LegacyComponentSerializer.legacySection().serialize(displayName);
        if (legacyDisplayName.length() > 16) legacyDisplayName = legacyDisplayName.substring(0, 16);
        String legacyPrefix = LegacyComponentSerializer.legacySection().serialize(prefix);
        if (legacyPrefix.length() > 16) legacyPrefix = legacyPrefix.substring(0, 16);
        String legacySuffix = LegacyComponentSerializer.legacySection().serialize(suffix);
        if (legacySuffix.length() > 16) legacySuffix = legacySuffix.substring(0, 16);
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(new TeamPacket(scoreboard.getPriority(), name, (byte) 2, legacyDisplayName,
                    new ComponentHolder(player.getProtocolVersion(), displayName), legacyPrefix,
                    new ComponentHolder(player.getProtocolVersion(), prefix), legacySuffix,
                    new ComponentHolder(player.getProtocolVersion(), suffix), nameVisibility, collisionRule, color, getFlags(), null));
        }
    }

    private void sendModifyEntry(@NonNull String entry, boolean add) {
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(TeamPacket.addOrRemovePlayer(scoreboard.getPriority(), name, entry, add));
        }
    }

    private byte getFlags() {
        byte flags = 0;
        if (allowFriendlyFire) flags += 1;
        if (canSeeFriendlyInvisibles) flags += 2;
        return flags;
    }

    public void unregister() {
        checkState();
        registered = false;
        for (ConnectedPlayer player : scoreboard.getPlayers()) {
            player.getConnection().write(TeamPacket.unregister(scoreboard.getPriority(), name));
        }
    }

    private void checkState() {
        if (!registered) throw new IllegalStateException("This team was unregistered");
    }
}
