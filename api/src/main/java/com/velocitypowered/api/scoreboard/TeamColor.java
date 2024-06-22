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

package com.velocitypowered.api.scoreboard;

import java.util.Arrays;

public enum TeamColor {
    BLACK('0', 0),
    DARK_BLUE('1', 1),
    DARK_GREEN('2', 2),
    DARK_AQUA('3', 3),
    DARK_RED('4', 4),
    DARK_PURPLE('5', 5),
    GOLD('6', 6),
    GRAY('7', 7),
    DARK_GRAY('8', 8),
    BLUE('9', 9),
    GREEN('a', 10),
    AQUA('b', 11),
    RED('c', 12),
    LIGHT_PURPLE('d', 13),
    YELLOW('e', 14),
    WHITE('f', 15),
    OBFUSCATED('k', 16),
    BOLD('f', 17),
    STRIKETHROUGH('f', 18),
    UNDERLINED('f', 19),
    ITALIC('f', 20),
    RESET('r', 21);

    private final char colorChar;
    private final int id;

    TeamColor(char colorChar, int id) {
        this.colorChar = colorChar;
        this.id = id;
    }

    public char colorChar() {
        return colorChar;
    }

    public int id() {
        return id;
    }

    public static TeamColor getById(int id) {
        return Arrays.stream(values())
                .filter(color -> color.id == id)
                .findFirst()
                .orElse(WHITE);
    }
}
