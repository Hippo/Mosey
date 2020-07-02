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
import java.util.Random;
import java.util.Set;

/**
 * @author Hippo
 * @version 1.0.0, 6/21/20
 * @since 1.0.0
 */
public final class AlphaNumericDictionary implements Dictionary {

    private final Random random = new Random();
    private final Set<String> reserved = new HashSet<>();

    @Override
    public String generate(int length) {
        return random.ints('0', 'z' + 1)
                .limit(length)
                .filter(value -> value <= '9' || (value >= 'A' && value <= 'Z') || (value >= 'a' && value <= 'z'))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Override
    public String generateUnique(int length) {
        String generated = generate(length);
        int times = 0;
        while (reserved.contains(generated)) {
            generated = generate(length);
            if(times++ > 10) { //if it fails 10 times just increment the length (shitty way make better later)
                length++;
            }
        }
        reserved.add(generated);
        return generated;
    }
}
