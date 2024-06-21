package com.velocitypowered.api.event.scoreboard;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract event class for all scoreboard-related events.
 */
public abstract class ScoreboardEvent {

    /** Player who received the scoreboard change */
    @NotNull
    private final Player player;

    /**
     * Constructs new instance with given player.
     *
     * @param   player
     *          Player who received the change
     */
    protected ScoreboardEvent(@NotNull Player player) {
        this.player = player;
    }

    /**
     * Returns player who received the scoreboard change.
     *
     * @return  player who received the scoreboard change
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }
}
