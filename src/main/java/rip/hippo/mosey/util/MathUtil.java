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

package rip.hippo.mosey.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Hippo
 * @version 1.0.0, 6/24/20
 * @since 1.0.0
 */
public enum MathUtil {
    ;
    private static final Random RANDOM = new SecureRandom();


    public static int generate(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }

    public static boolean chance(int percentage) {
        return percentage >= generate(0, 101);
    }

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    public static int randomInt(int bound) {
        return RANDOM.nextInt(bound);
    }

}
