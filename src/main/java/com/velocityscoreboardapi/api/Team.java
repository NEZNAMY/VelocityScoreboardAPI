package com.velocityscoreboardapi.api;

import com.velocityscoreboardapi.impl.VelocityTeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Team {

    @NonNull
    static Team.Builder builder() {
        return new VelocityTeam.Builder();
    }

    @NotNull
    String getName();

    @NotNull
    Component getDisplayName();

    void setDisplayName(@NonNull Component displayName);

    @NotNull
    Component getPrefix();

    void setPrefix(@NonNull Component prefix);

    @NotNull
    Component getSuffix();

    void setSuffix(@NonNull Component suffix);

    @NotNull
    NameVisibility getNameVisibility();

    void setNameVisibility(@NonNull NameVisibility visibility);

    @NotNull
    CollisionRule getCollisionRule();

    void setCollisionRule(@NonNull CollisionRule collisionRule);

    int getColor();

    void setColor(int color);

    boolean isAllowFriendlyFire();

    void setAllowFriendlyFire(boolean friendlyFire);

    boolean isCanSeeFriendlyInvisibles();

    void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

    @NotNull
    Collection<String> getEntries();

    void addEntry(@NonNull String entry);

    void removeEntry(@NonNull String entry);

    interface Builder {

        @NotNull
        Builder displayName(@NonNull Component displayName);

        @NotNull
        Builder prefix(@NonNull Component prefix);

        @NotNull
        Builder suffix(@NonNull Component suffix);

        @NotNull
        Builder nameVisibility(@NonNull NameVisibility visibility);

        @NotNull
        Builder collisionRule(@NonNull CollisionRule collisionRule);

        @NotNull
        Builder color(int color);

        @NotNull
        Builder allowFriendlyFire(boolean friendlyFire);

        @NotNull
        Builder canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

        @NotNull
        Builder entries(@NonNull Collection<String> entries);

        @NotNull
        Team build(@NonNull Scoreboard scoreboard);

    }

}
