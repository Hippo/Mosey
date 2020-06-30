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

package rip.hippo.mosey.jar.resource.impl;

import rip.hippo.mosey.jar.resource.Resource;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public class JarResource implements Resource {
    private final String name;
    private final byte[] resourceBytes;

    public JarResource(String name, byte[] resourceBytes) {
        this.name = name;
        this.resourceBytes = resourceBytes;
    }

    @Override
    public byte[] toByteArray() {
        return resourceBytes;
    }

    @Override
    public String getName() {
        return name;
    }
}
