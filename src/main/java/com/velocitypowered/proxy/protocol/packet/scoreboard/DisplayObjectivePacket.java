package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocityscoreboardapi.api.DisplaySlot;
import com.velocityscoreboardapi.internal.PacketHandler;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * Display objective packet for assigning slot to objectives.
 */
public class DisplayObjectivePacket implements MinecraftPacket {

    /** Packet priority (higher value = higher priority) */
    private final int packetPriority;

    /** Display slot */
    private DisplaySlot position;

    /** Name of this objective (up to 16 characters) */
    private String objectiveName;

    /**
     * Constructs new instance for packet decoding.
     */
    public DisplayObjectivePacket() {
        this.packetPriority = 0;
    }

    /**
     * Constructs new instance for packet sending.
     *
     * @param   packetPriority
     *          Priority of this packet
     * @param   position
     *          Display slot
     * @param   objectiveName
     *          Objective name
     */
    public DisplayObjectivePacket(int packetPriority, @NotNull DisplaySlot position, @NotNull String objectiveName) {
        this.packetPriority = packetPriority;
        this.position = position;
        this.objectiveName = objectiveName;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_2)) {
            position = DisplaySlot.values()[ProtocolUtils.readVarInt(buf)]; //TODO something to prevent new array creation each time?
        } else {
            position = DisplaySlot.values()[buf.readByte()]; //TODO something to prevent new array creation each time?
        }
        objectiveName = ProtocolUtils.readString(buf);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_20_2)) {
            ProtocolUtils.writeVarInt(buf, position.ordinal());
        } else {
            buf.writeByte(position.ordinal());
        }
        ProtocolUtils.writeString(buf, objectiveName);
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return PacketHandler.handle(minecraftSessionHandler, this);
    }

    public int getPacketPriority() {
        return packetPriority;
    }

    @NotNull
    public DisplaySlot getPosition() {
        return position;
    }

    @NotNull
    public String getObjectiveName() {
        return objectiveName;
    }
}