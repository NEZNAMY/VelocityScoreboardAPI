package com.velocityscoreboardapi.internal;

import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocityscoreboardapi.impl.downstream.DownstreamScoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataHolder {

    private static final Map<MinecraftSessionHandler, DownstreamScoreboard> downstreamScoreboards = new ConcurrentHashMap<>();

    @NotNull
    public static DownstreamScoreboard getDownstreamScoreboard(@NotNull MinecraftSessionHandler handler) {
        return downstreamScoreboards.computeIfAbsent(handler, h -> new DownstreamScoreboard());
    }
}
