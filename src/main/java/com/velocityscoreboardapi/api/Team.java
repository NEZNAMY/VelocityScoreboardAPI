package com.velocityscoreboardapi.api;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Team {

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
}
