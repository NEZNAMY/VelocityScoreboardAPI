package com.velocityscoreboardapi.api;

import com.velocityscoreboardapi.impl.VelocityTeam;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Team {

    @NotNull
    static Team.Builder builder(@NotNull String name) {
        return new VelocityTeam.Builder(name);
    }

    @NotNull
    String getName();

    @NotNull
    Component getDisplayName();

    void setDisplayName(@NotNull Component displayName);

    @NotNull
    Component getPrefix();

    void setPrefix(@NotNull Component prefix);

    @NotNull
    Component getSuffix();

    void setSuffix(@NotNull Component suffix);

    @NotNull
    NameVisibility getNameVisibility();

    void setNameVisibility(@NotNull NameVisibility visibility);

    @NotNull
    CollisionRule getCollisionRule();

    void setCollisionRule(@NotNull CollisionRule collisionRule);

    int getColor();

    void setColor(int color);

    boolean isAllowFriendlyFire();

    void setAllowFriendlyFire(boolean friendlyFire);

    boolean isCanSeeFriendlyInvisibles();

    void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

    @NotNull
    Collection<String> getEntries();

    void addEntry(@NotNull String entry);

    void removeEntry(@NotNull String entry);

    interface Builder {

        @NotNull
        Builder name(@NotNull String name);

        @NotNull
        Builder displayName(@NotNull Component displayName);

        @NotNull
        Builder prefix(@NotNull Component prefix);

        @NotNull
        Builder suffix(@NotNull Component suffix);

        @NotNull
        Builder nameVisibility(@NotNull NameVisibility visibility);

        @NotNull
        Builder collisionRule(@NotNull CollisionRule collisionRule);

        @NotNull
        Builder color(int color);

        @NotNull
        Builder allowFriendlyFire(boolean friendlyFire);

        @NotNull
        Builder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

        @NotNull
        Builder entries(@NotNull Collection<String> entries);

        @NotNull
        Team build(@NotNull Scoreboard scoreboard);

    }

}
