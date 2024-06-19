package com.velocitypowered.proxy.protocol.packet.scoreboard;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.Either;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamPacket implements MinecraftPacket {

    private boolean decodedFromDownstream;
    private String name;
    private byte mode;
    private Either<String, ComponentHolder> displayName;
    private Either<String, ComponentHolder> prefix;
    private Either<String, ComponentHolder> suffix;
    private String nameTagVisibility;
    private String collisionRule;
    private int color;
    private byte friendlyFire;
    private String[] players;

    /**
     * Packet to destroy a team.
     *
     * @param name team name
     */
    public TeamPacket(String name) {
        this.name = name;
        this.mode = 1;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        decodedFromDownstream = true;
        name = ProtocolUtils.readString(buf);
        mode = buf.readByte();
        if (mode == 0 || mode == 2) {
            if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_13)) {
                displayName = Either.left(ProtocolUtils.readString(buf));
                prefix = Either.left(ProtocolUtils.readString(buf));
                suffix = Either.left(ProtocolUtils.readString(buf));
            } else {
                displayName = Either.right(ComponentHolder.read(buf, protocolVersion));
            }
            friendlyFire = buf.readByte();
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                nameTagVisibility = ProtocolUtils.readString(buf);
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_9)) {
                collisionRule = ProtocolUtils.readString(buf);
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                color = ProtocolUtils.readVarInt(buf);
                prefix = Either.right(ComponentHolder.read(buf, protocolVersion));
                suffix = Either.right(ComponentHolder.read(buf, protocolVersion));
            } else if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                color = buf.readByte();
            }
        }
        if (mode == 0 || mode == 3 || mode == 4) {
            int len = protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8) ? ProtocolUtils.readVarInt(buf) : buf.readShort();
            players = new String[len];
            for (int i = 0; i < len; i++) {
                players[i] = ProtocolUtils.readString(buf);
            }
        }
    }

    @Override
    public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion protocolVersion) {
        ProtocolUtils.writeString(buf, name);
        buf.writeByte(mode);
        if (mode == 0 || mode == 2) {
            if (protocolVersion.lessThan(ProtocolVersion.MINECRAFT_1_13)) {
                ProtocolUtils.writeString(buf, displayName.getLeft());
                ProtocolUtils.writeString(buf, prefix.getLeft());
                ProtocolUtils.writeString(buf, suffix.getLeft());
            } else {
                displayName.getRight().write(buf);
            }
            buf.writeByte(friendlyFire);
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                ProtocolUtils.writeString(buf, nameTagVisibility);
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_9)) {
                ProtocolUtils.writeString(buf, collisionRule);
            }
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_13)) {
                ProtocolUtils.writeVarInt(buf, color);
                prefix.getRight().write(buf);
                suffix.getRight().write(buf);
            } else if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                buf.writeByte(color);
            }
        }
        if (mode == 0 || mode == 3 || mode == 4) {
            if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
                ProtocolUtils.writeVarInt(buf, players.length);
            } else {
                buf.writeShort(players.length);
            }
            for (String player : players) {
                ProtocolUtils.writeString(buf, player);
            }
        }
    }

    @Override
    public boolean handle(MinecraftSessionHandler minecraftSessionHandler) {
        return false;
    }
}