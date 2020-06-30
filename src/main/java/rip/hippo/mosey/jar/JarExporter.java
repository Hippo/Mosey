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

package rip.hippo.mosey.jar;

import rip.hippo.mosey.jar.resource.Resource;
import rip.hippo.mosey.jar.resource.ResourceManager;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public final class JarExporter {

    public void exportJar(ResourceManager resourceManager, File output) {
        Logger.info("Attempting to export jar.");
        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(output))) {
            for (Resource resource : resourceManager.getResources()) {
                JarEntry jarEntry = new JarEntry(resource.getName());
                jarEntry.setCompressedSize(-1);
                jarOutputStream.putNextEntry(jarEntry);
                jarOutputStream.write(resource.toByteArray());
                jarOutputStream.closeEntry();
            }
        } catch (IOException e) {
            Logger.error(e, String.format("Failed to write output file to %s", output.getAbsolutePath()));
        }
        Logger.info(String.format("Successfully written jar to %s", output.getAbsolutePath()));
    }
}
