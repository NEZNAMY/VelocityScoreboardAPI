/*
 * This file is part of VelocityScoreboardAPI, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) NEZNAMY <n.e.z.n.a.m.y@azet.sk>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.velocitypowered.proxy.data;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.scoreboard.NumberFormat;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.kyori.adventure.text.serializer.nbt.NBTComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Class for encoding / decoding number formats. If the Scoreboard API gets merged into Velocity,
 * these methods will be moved into {@link ProtocolUtils} class.
 */
public class NumberFormatEncoder {

    /**
     * Reads NumberFormat from buffer.
     *
     * @param   buf
     *          Buffer to read from
     * @param   ver
     *          Protocol version used to encode the format
     * @return  Decoded Number Format
     */
    @NotNull
    public static NumberFormat read(@NonNull ByteBuf buf, @NonNull ProtocolVersion ver) {
        int format = ProtocolUtils.readVarInt(buf);
        return switch (format) {
            case 0 -> NumberFormat.BlankFormat.INSTANCE;
            case 1 -> new NumberFormat.StyledFormat(NBTComponentSerializer.nbt().deserializeStyle(ProtocolUtils.readCompoundTag(buf, ver, null)));
            case 2 -> new DeserializedFixedFormat(ComponentHolder.read(buf, ver));
            default -> throw new IllegalArgumentException("Unknown number format " + format);
        };
    }

    /**
     * Writes a NumberFormat to buffer.
     *
     * @param   buf
     *          Buffer to write number format to
     * @param   ver
     *          Protocol version to write format for
     * @param   format
     *          NumberFormat to write
     */
    public static void write(@NonNull ByteBuf buf, @NonNull ProtocolVersion ver, @NonNull NumberFormat format) {
        if (format instanceof NumberFormat.BlankFormat) {
            ProtocolUtils.writeVarInt(buf, 0);
        } else if (format instanceof NumberFormat.StyledFormat styled) {
            ProtocolUtils.writeVarInt(buf, 1);
            ProtocolUtils.writeBinaryTag(buf, ver, NBTComponentSerializer.nbt().serializeStyle(styled.style()));
        } else if (format instanceof NumberFormat.FixedFormat fixed) {
            ProtocolUtils.writeVarInt(buf, 2);
            new ComponentHolder(ver, fixed.component()).write(buf);
        } else if (format instanceof DeserializedFixedFormat deserialized) {
            ProtocolUtils.writeVarInt(buf, 2);
            deserialized.holder.write(buf);
        } else throw new IllegalArgumentException("Unknown number format type " + format.getClass().getName());
    }

    private record DeserializedFixedFormat(@NonNull ComponentHolder holder) implements NumberFormat {
    }
}
