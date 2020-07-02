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

package rip.hippo.mosey;

import rip.hippo.mosey.configuration.Configuration;
import rip.hippo.mosey.jar.JarExporter;
import rip.hippo.mosey.jar.JarLoader;
import rip.hippo.mosey.jar.impl.StandardJarExporter;
import rip.hippo.mosey.jar.impl.StandardJarLoader;
import rip.hippo.mosey.jar.resource.Resource;
import rip.hippo.mosey.jar.resource.ResourceManager;
import rip.hippo.mosey.jar.resource.impl.ClassResource;
import rip.hippo.mosey.transformer.TransformerManager;
import org.tinylog.Logger;

import java.io.File;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public final class Mosey {

    private final JarLoader jarLoader;
    private final JarExporter jarExporter;
    private final Configuration configuration;
    private final ResourceManager resourceManager;
    private final TransformerManager transformerManager;

    public Mosey(Configuration configuration, JarLoader jarLoader, JarExporter jarExporter) {
        this.configuration = configuration;
        this.jarLoader = jarLoader;
        this.jarExporter = jarExporter;
        this.resourceManager = new ResourceManager();
        this.transformerManager = new TransformerManager(configuration, resourceManager);
    }

    public Mosey(Configuration configuration) {
        this(configuration, new StandardJarLoader(), new StandardJarExporter());
    }

    void loadRuntime() {
        loadJar(configuration.getRuntime(), true);
    }

    void loadInput() {
        loadJar(configuration.getInput(), false);
    }

    void loadJar(File input, boolean library) {
        jarLoader.loadJar(input, resourceManager, library);
    }

    void transform() {
        Logger.info("Transforming jar...");
        for (Resource resource : resourceManager.getResources()) {
            if(resource instanceof ClassResource) {
                ClassResource classResource = (ClassResource) resource;
                transformerManager.transform(classResource.getClassWrapper());
            }
        }
        Logger.info("Jar transformed");
    }

    void exportJar() {
        jarExporter.exportJar(resourceManager, configuration.getOutput());
    }
}
