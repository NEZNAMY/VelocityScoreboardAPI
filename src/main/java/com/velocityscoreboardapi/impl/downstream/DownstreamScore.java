package com.velocityscoreboardapi.impl.downstream;

import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScorePacket;
import com.velocityscoreboardapi.api.NumberFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DownstreamScore {

    @NotNull private final String holder;
    private int score;
    @Nullable private ComponentHolder displayName;
    @Nullable private NumberFormat numberFormat;

    public DownstreamScore(@NotNull String holder, int score, @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        this.holder = holder;
        this.score = score;
        this.displayName = displayName;
        this.numberFormat = numberFormat;
    }

    public void update(@NotNull ScorePacket packet) {
        score = packet.getValue();
        displayName = packet.getDisplayName();
        numberFormat = packet.getNumberFormat();
    }
}
