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

package rip.hippo.mosey.jar.resource;


import java.util.LinkedList;
import java.util.List;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public class ResourceManager {
    private final List<Resource> resources;

    public ResourceManager() {
        this.resources = new LinkedList<>();
    }

    public void addResource(Resource resource) {
        this.resources.add(resource);
    }

    public List<Resource> getResources() {
        return resources;
    }
}
