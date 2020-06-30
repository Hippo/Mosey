/*
 * Mosey is a free and open source java bytecode obfuscator.
 *     Copyright (C) 2020  Hippo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package rip.hippo.mosey.dictionary.impl;

import rip.hippo.mosey.dictionary.Dictionary;
import rip.hippo.mosey.util.MathUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Hippo
 * @version 1.0.0, 6/21/20
 * @since 1.0.0
 */
public final class AlphaNumericDictionary implements Dictionary {

    private static final char[] CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
    private final Set<String> reserved = new HashSet<>();

    @Override
    public String generate(int length) {
        char[] sequence = new char[length];
        for (int i = 0; i < length; i++) {
            sequence[i] = CHARSET[MathUtil.randomInt(CHARSET.length)];
        }
        return new String(sequence);
    }

    @Override
    public String generateUnique(int length) {
        String generated = generate(length);
        int times = 0;
        while (reserved.contains(generated)) {
            generated = generate(length);
            if(times++ > 10) { //if it fails 10 times just increment the length
                length++;
            }
        }
        reserved.add(generated);
        return generated;
    }
}
