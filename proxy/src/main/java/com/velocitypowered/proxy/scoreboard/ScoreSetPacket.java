package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import com.velocityscoreboardapi.api.NumberFormat;
import com.velocityscoreboardapi.internal.PacketHandler;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Score packet set for 1.20.3+ players.
 */
public class ScoreSetPacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Score holder who the score belongs to */
    private String scoreHolder;

    /** Objective from which the holder should be removed (null for all objectives ?) */
    private String objectiveName;

    /** Score value */
    private int value;

    /** Display name to use for score holder instead of name (1.20.3+) */
    @Nullable
    private ComponentHolder displayName;

    /** Number format of the score, null to use default number format from objective (1.20.3+) */
    @Nullable
    private NumberFormat numberFormat;

    /**
     * Constructs new instance for packet decoding.
     */
    public ScoreSetPacket() {
        this.packetPriority = 0;
    }

    /**
     * Constructs new instance for packet sending.
     *
     * @param   packetPriority
     *          Packet priority
     * @param   scoreHolder
     *          Score holder
     * @param   objectiveName
     *          Objective name
     * @param   value
     *          Score value
     * @param   displayName
     *          Holder's display name (1.20.3+)
     * @param   numberFormat
     *          Number format of the score (1.20.3+)
     */
    public ScoreSetPacket(int packetPriority, @NotNull String scoreHolder, @NotNull String objectiveName,
                          int value, @Nullable ComponentHolder displayName, @Nullable NumberFormat numberFormat) {
        this.packetPriority = packetPriority;
        this.scoreHolder = scoreHolder;
        this.objectiveName = objectiveName;
        this.value = value;
        this.displayName = displayName;
        this.numberFormat = numberFormat;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        scoreHolder = ProtocolUtils.readString(buf);
        objectiveName = ProtocolUtils.readString(buf);
        value = ProtocolUtils.readVarInt(buf);
        if (buf.readBoolean()) displayName = ComponentHolder.read(buf, protocolVersion);
        if (buf.readBoolean()) numberFormat = NumberFormat.read(buf, protocolVersion);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, scoreHolder);
        ProtocolUtils.writeString(buf, objectiveName);
        ProtocolUtils.writeVarInt(buf, value);
        buf.writeBoolean(displayName != null);
        if (displayName != null) displayName.write(buf);
        buf.writeBoolean(numberFormat != null);
        if (numberFormat != null) numberFormat.write(buf, protocolVersion);
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

    @NotNull
    public String getObjectiveName() {
        return objectiveName;
    }

    public int getValue() {
        return value;
    }

    @Nullable
    public ComponentHolder getDisplayName() {
        return displayName;
    }

    @Nullable
    public NumberFormat getNumberFormat() {
        return numberFormat;
    }
}
