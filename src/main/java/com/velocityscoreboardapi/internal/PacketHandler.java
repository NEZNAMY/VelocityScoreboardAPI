package com.velocityscoreboardapi.internal;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.connection.client.ClientPlaySessionHandler;
import com.velocitypowered.proxy.protocol.packet.scoreboard.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class PacketHandler {

    private static final Field playerField;

    static {
        try {
            playerField = ClientPlaySessionHandler.class.getDeclaredField("player");
            playerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @SneakyThrows
    private Player getPlayer(@NotNull MinecraftSessionHandler handler) {
        return (Player) playerField.get(handler);
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull DisplayObjectivePacket packet) {
        return false;
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ObjectivePacket packet) {
        return false;
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ScorePacket packet) {
        return false;
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull ScoreResetPacket packet) {
        return false;
    }

    public static boolean handle(@NotNull MinecraftSessionHandler handler, @NotNull TeamPacket packet) {
        return false;
    }
}
