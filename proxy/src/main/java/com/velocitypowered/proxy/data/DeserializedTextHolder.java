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

import com.velocitypowered.api.TextHolder;
import com.velocitypowered.proxy.protocol.packet.chat.ComponentHolder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * TextHolder override for instances deserialized in packet decoder.
 * Keeping the component holder without having to convert it to component saves resources,
 * which has lately been an active topic on BungeeCord GitHub.
 */
public class DeserializedTextHolder extends TextHolder {

    /** Deserialized component holder */
    @NotNull
    private final ComponentHolder holder;

    /**
     * Constructs new instance with given component holder
     *
     * @param   holder
     *          Deserialized component holder
     */
    public DeserializedTextHolder(@NotNull ComponentHolder holder) {
        super("", Component.empty());
        this.holder = holder;
    }

    /**
     * Returns component holder.
     *
     * @return  Component holder
     */
    @NotNull
    public ComponentHolder getHolder() {
        return holder;
    }

    @NotNull
    @Override
    public Component getModernText() {
        if (modernText == Component.empty()) modernText = holder.getComponent(); // Compute on request
        return modernText; // No it is not null
    }
}
