package com.velocityscoreboardapi.impl;

import com.google.common.collect.Lists;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket;
import com.velocitypowered.proxy.protocol.packet.scoreboard.TeamPacket.TeamAction;
import com.velocityscoreboardapi.api.CollisionRule;
import com.velocityscoreboardapi.api.NameVisibility;
import com.velocityscoreboardapi.api.Scoreboard;
import com.velocityscoreboardapi.api.Team;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VelocityTeam implements Team {

    static final int DEFAULT_COLOR = 21;

    @NonNull
    private final VelocityScoreboard scoreboard;
    @NonNull
    private final String name;
    @NonNull
    private Component displayName;
    @NonNull
    private Component prefix;
    @NonNull
    private Component suffix;
    @NonNull
    private NameVisibility nameVisibility;
    @NonNull
    private CollisionRule collisionRule;
    private int color; // Cannot use NamedTextColor because it does not have ordinals + does not support magic codes or even reset
    boolean allowFriendlyFire;
    boolean canSeeFriendlyInvisibles;
    @NotNull
    private final Collection<String> entries;
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
            player.getConnection().write(new TeamPacket(scoreboard.getPriority(), TeamAction.REGISTER, name, legacyDisplayName,
                    new ComponentHolder(player.getProtocolVersion(), displayName), legacyPrefix,
                    new ComponentHolder(player.getProtocolVersion(), prefix), legacySuffix,
                    new ComponentHolder(player.getProtocolVersion(), suffix), nameVisibility, collisionRule, color, getFlags(), entries));
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
            player.getConnection().write(new TeamPacket(scoreboard.getPriority(), TeamAction.UPDATE, name, legacyDisplayName,
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

    public static class Builder implements Team.Builder {

        private String name;
        private Component displayName;
        @NonNull private Component prefix = Component.empty();
        @NonNull private Component suffix = Component.empty();
        @NonNull private NameVisibility nameVisibility = NameVisibility.ALWAYS;
        @NonNull private CollisionRule collisionRule = CollisionRule.ALWAYS;
        private int color = DEFAULT_COLOR;
        private boolean allowFriendlyFire = true;
        private boolean canSeeFriendlyInvisibles = false;
        @NonNull private Collection<String> entries = Lists.newArrayList();

        public Builder(@NonNull String name) {
            this.name = name;
            this.displayName = Component.text(name);
        }

        @NonNull
        public Builder name(@NonNull String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder displayName(@NonNull Component displayName) {
            this.displayName = displayName;
            return this;
        }

        @NonNull
        public Builder prefix(@NonNull Component prefix) {
            this.prefix = prefix;
            return this;
        }

        @NonNull
        public Builder suffix(@NonNull Component suffix) {
            this.suffix = suffix;
            return this;
        }

        @NonNull
        public Builder nameVisibility(@NonNull NameVisibility nameVisibility) {
            this.nameVisibility = nameVisibility;
            return this;
        }

        @NonNull
        public Builder collisionRule(@NonNull CollisionRule collisionRule) {
            this.collisionRule = collisionRule;
            return this;
        }

        @NonNull
        public Builder color(int color) {
            this.color = color;
            return this;
        }

        @NonNull
        public Builder allowFriendlyFire(boolean allowFriendlyFire) {
            this.allowFriendlyFire = allowFriendlyFire;
            return this;
        }

        @NonNull
        public Builder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
            this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
            return this;
        }

        @NonNull
        public Builder entries(@NonNull Collection<String> entries) {
            this.entries = entries;
            return this;
        }

        @NonNull
        public Team build(@NonNull Scoreboard scoreboard) {
            return new VelocityTeam(
                    (VelocityScoreboard) scoreboard, name, displayName, prefix, suffix, nameVisibility, collisionRule,
                    color, allowFriendlyFire, canSeeFriendlyInvisibles, entries, true
            );
        }
    }
}
