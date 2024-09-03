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
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is a class for holding a string collection that most likely
 * only consists of a single entry. It has fields for both single entry
 * and a collection, with internal logic deciding which one to use
 * in methods. This avoids creating collections and iterators when only a single
 * string is present.
 */
public class StringCollection {

    /**
     * Present entry if and only if this collection only contains exactly one entry. If this collection contains
     * either 0 or more than 1 entry, this field is {@code null}.
     */
    @Nullable
    private String entry;

    /**
     * Entry collection to use if more than 1 entry is present. It may or may not be null if less than 2 entries
     * are present, based on whether a collection was requested externally or not.
     * If it was, the value is saved as there is no point in keeping it null anymore.
     */
    @Nullable
    private List<String> entries;

    /**
     * Constructs new instance with empty collection.
     */
    public StringCollection() {
    }

    /**
     * Constructs a new instance with just a single entry.
     *
     * @param   entry
     *          Entry in collection
     */
    public StringCollection(@NotNull String entry) {
        this.entry = entry;
    }

    /**
     * Constructs a new instance with given collection.
     *
     * @param   entries
     *          Entries in collection
     */
    public StringCollection(@NotNull Collection<String> entries) {
        if (entries.size() == 1) {
            this.entry = entries.iterator().next();
        } else {
            this.entries = new ArrayList<>(entries); // Clone to prevent external modifications
        }
    }

    /**
     * Constructs new instance using given buffer and protocol version.
     * Variables are initialized based on the amount of entries in the buffer.
     *
     * @param   buf
     *          Buffer to read from
     * @param   protocolVersion
     *          Protocol version used to encode the entries
     */
    public StringCollection(@NotNull ByteBuf buf, @NotNull ProtocolVersion protocolVersion) {
        int len = protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8) ? ProtocolUtils.readVarInt(buf) : buf.readShort();
        if (len == 0) return;
        if (len == 1) {
            entry = ProtocolUtils.readString(buf);
        } else {
            entries = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                entries.add(ProtocolUtils.readString(buf));
            }
        }
    }

    /**
     * Writes the collection to a protocol buffer.
     *
     * @param   buf
     *          Buffer to write to
     * @param   protocolVersion
     *          Protocol version for encoding
     */
    public void write(@NotNull ByteBuf buf, @NotNull ProtocolVersion protocolVersion) {
        int size = size();
        if (protocolVersion.noLessThan(ProtocolVersion.MINECRAFT_1_8)) {
            ProtocolUtils.writeVarInt(buf, size);
        } else {
            buf.writeShort(size);
        }
        if (size == 0) return;
        if (size == 1) {
            ProtocolUtils.writeString(buf, entry);
        } else {
            for (String player : entries) {
                ProtocolUtils.writeString(buf, player);
            }
        }
    }

    /**
     * Returns size of this collection.
     *
     * @return  size of this collection
     */
    public int size() {
        if (entry != null) return 1;
        return entries == null ? 0 : entries.size();
    }

    /**
     * Returns single entry in this collection. If this collection does not contain
     * exactly 1 element, {@code null} is returned
     *
     * @return  The only entry in this collection or {@code null} if it does not contain exactly 1 entry
     */
    @Nullable
    public String getEntry() {
        return entry;
    }

    /**
     * Returns collection of entries.
     *
     * @return  collection of entries
     */
    @NotNull
    public Collection<String> getEntries() {
        if (size() == 0) return Collections.emptyList();
        if (entries == null) {
            entries = new ArrayList<>();
            if (entry != null) entries.add(entry);
        }
        return entries;
    }

    /**
     * Returns {@code true} if this collection contains given entry, {@code false} if not.
     *
     * @param   entry
     *          Entry to check for
     * @return  {@code true} if this collection contains given entry, {@code false} if not
     */
    public boolean contains(@NotNull String entry) {
        if (this.entry != null && this.entry.equals(entry)) return true;
        return entries != null && entries.contains(entry);
    }

    /**
     * Adds entry to this collection.
     *
     * @param   entry
     *          Entry to add
     */
    public void add(@NotNull String entry) {
        int size = size();
        if (size == 0) {
            this.entry = entry;
        } else if (size == 1) {
            getEntries(); // Create collection from single entry
            if (!entries.contains(entry)) entries.add(entry);
            this.entry = null;
        } else {
            entries.add(entry);
        }
    }

    /**
     * Removes entry from this collection. Returns {@code true} if entry was present and removed,
     * {@code false} otherwise.
     *
     * @param   entry
     *          Entry to remove
     * @return  {@code true} if entry was present and removed, {@code false} otherwise
     */
    public boolean remove(@NotNull String entry) {
        boolean removed = false;
        if (entry.equals(this.entry)) {
            this.entry = null;
            removed = true;
        }
        if (entries != null && entries.remove(entry)) {
            if (entries.size() == 1) {
                this.entry = entry;
            }
            removed = true;
        }
        return removed;
    }

    /**
     * Adds all unique elements from the specified collection to this one.
     *
     * @param   entries
     *          Entries to add to this collection
     */
    public void addAll(@NotNull StringCollection entries) {
        int size = entries.size();
        if (size == 0) return;
        if (size == 1) {
            add(entries.getEntry());
        } else {
            for (String entry : entries.getEntries()) {
                add(entry);
            }
        }
    }

    /**
     * Removes all entries in specified collection from this one.
     *
     * @param   entries
     *          Entries to remove from this collection
     */
    public void removeAll(@NotNull StringCollection entries) {
        int size = entries.size();
        if (size == 0) return;
        if (size == 1) {
            remove(entries.getEntry());
        } else {
            for (String entry : entries.getEntries()) {
                remove(entry);
            }
        }
    }
}
