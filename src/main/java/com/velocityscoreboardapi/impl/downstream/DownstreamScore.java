package com.velocityscoreboardapi.impl.downstream;

import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocitypowered.proxy.protocol.packet.scoreboard.ScorePacket;
import com.velocityscoreboardapi.api.NumberFormat;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public class DownstreamScore {

    @NonNull private final String holder;
    private int score;
    @Nullable private ComponentHolder displayName;
    @Nullable private NumberFormat numberFormat;

    public void update(@NonNull ScorePacket packet) {
        score = packet.getValue();
        displayName = packet.getDisplayName();
        numberFormat = packet.getNumberFormat();
    }
}
