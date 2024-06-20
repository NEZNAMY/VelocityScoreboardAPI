package com.velocitypowered.proxy.scoreboard;

import org.jetbrains.annotations.NotNull;

public class PacketLogger {

    public static void invalidDownstreamPacket(@NotNull String message) {
        System.out.println(message);
    }
}
