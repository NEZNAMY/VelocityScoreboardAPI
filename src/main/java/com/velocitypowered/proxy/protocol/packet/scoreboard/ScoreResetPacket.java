package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocityscoreboardapi.internal.PacketHandler;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Score reset packet for 1.20.3+ players.
 */
public class ScoreResetPacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Score holder who the score belongs to */
    private String scoreHolder;

    /** Objective from which the holder should be removed (null for all objectives ?) */
    private String objectiveName;

    /**
     * Constructs new instance for packet decoding.
     */
    public ScoreResetPacket() {
        this.packetPriority = 0;
    }

    /**
     * Constructs new instance for packet sending.
     *
     * @param   packetPriority
     *          Priority of this packet
     * @param   scoreHolder
     *          Score holder
     * @param   objectiveName
     *          Objective name
     */
    public ScoreResetPacket(int packetPriority, @NotNull String scoreHolder, @Nullable String objectiveName) {
        this.packetPriority = packetPriority;
        this.scoreHolder = scoreHolder;
        this.objectiveName = objectiveName;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        scoreHolder = ProtocolUtils.readString(buf);
        if (buf.readBoolean()) objectiveName = ProtocolUtils.readString(buf);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, scoreHolder);
        buf.writeBoolean(objectiveName != null);
        if (objectiveName != null) ProtocolUtils.writeString(buf, objectiveName);
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }

    public int getPacketPriority() {
        return packetPriority;
    }

    @NotNull
    public String getScoreHolder() {
        return scoreHolder;
    }

    @Nullable
    public String getObjectiveName() {
        return objectiveName;
    }
}