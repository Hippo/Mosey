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

package rip.hippo.mosey.jar.impl;

import rip.hippo.mosey.asm.ClassHierarchy;
import rip.hippo.mosey.asm.wrapper.ClassWrapper;
import rip.hippo.mosey.jar.JarLoader;
import rip.hippo.mosey.jar.resource.ResourceManager;
import rip.hippo.mosey.jar.resource.impl.ClassResource;
import rip.hippo.mosey.jar.resource.impl.JarResource;
import rip.hippo.mosey.util.IOUtil;
import org.objectweb.asm.tree.ClassNode;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public final class StandardJarLoader implements JarLoader {

    @Override
    public void loadJar(File input, ResourceManager resourceManager, boolean library) {

        Logger.info(String.format("Loading %s " + (library ? "(library) " : ""), input.getAbsolutePath()));

        try (JarFile jarFile = new JarFile(input)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                byte[] bytes = IOUtil.toByteArray(jarFile, jarEntry);
                Logger.info(String.format("Loading resource %s", jarEntry.getName()));

                if (library) {
                    if (jarEntry.getName().contains(".class")) {
                        registerHierarchy(new ClassResource(bytes, true));
                    }
                }else {
                    resourceManager.addResource(jarEntry.getName().contains(".class") ? registerHierarchy(new ClassResource(bytes, false)) : new JarResource(jarEntry.getName(), bytes));
                }
            }
        } catch (IOException e) {
            Logger.error(e, String.format("Failed to read jar file %s.", input.getAbsolutePath()));
        }

        Logger.info("Resources loaded.\n");

    }

    private ClassResource registerHierarchy(ClassResource classResource) {
        ClassWrapper classWrapper = classResource.getClassWrapper();
        ClassHierarchy.registerClass(classWrapper);
        return classResource;
    }
}
