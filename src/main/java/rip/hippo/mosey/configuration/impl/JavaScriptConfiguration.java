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

package rip.hippo.mosey.configuration.impl;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import rip.hippo.mosey.configuration.Configuration;
import org.tinylog.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public final class JavaScriptConfiguration implements Configuration {

    private final File input, output, runtime;
    private final Collection<String> transformers;
    private final String dictionary;
    private final ScriptEngine scriptEngine;

    public JavaScriptConfiguration(String configPath) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        this.scriptEngine = scriptEngineManager.getEngineByExtension("js");

        try (FileReader fileReader = new FileReader(new File(configPath))) {
            Logger.info("Evaluating JavaScript config.");
            scriptEngine.eval(fileReader);
            Logger.info("Config evaluated.");
        }catch (IOException | ScriptException e) {
            Logger.error(e, String.format("Failed to evaluate config \"%s.\"", configPath));
        }

        String runtimePath = scriptEngine.get("runtime").toString();
        if(runtimePath.equals("detect")) {
            Logger.info("Trying to find Java Runtime (rt.jar)...");
            Optional<String> javaHome = Optional.ofNullable(System.getenv("JAVA_HOME"));
            runtimePath = javaHome.orElseThrow(() -> new RuntimeException("JAVA_HOME is not set."));
            runtimePath = runtimePath + "/jre/lib/rt.jar";
            Logger.info("Runtime found");
        }

        this.input = new File(scriptEngine.get("input").toString());
        this.output = new File(scriptEngine.get("output").toString());
        this.runtime = new File(runtimePath);
        this.transformers = ((ScriptObjectMirror) scriptEngine.get("transformers")).values().stream().map(Object::toString).collect(Collectors.toList());
        this.dictionary = scriptEngine.get("dictionary").toString();


        if(!input.exists()) {
            Logger.warn("Input file does not exist");
        }
        if(!runtime.exists()) {
            Logger.warn("Java Runtime does not exist, try manually setting it in the config.");
        }
    }

    @Override
    public File getInput() {
        return input;
    }

    @Override
    public File getOutput() {
        return output;
    }

    @Override
    public File getRuntime() {
        return runtime;
    }


    @Override
    public Collection<String> getTransformers() {
        return transformers;
    }

    @Override
    public String getDictionary() {
        return dictionary;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String object, String key) {
        return ((T) ((ScriptObjectMirror) scriptEngine.get(object)).get(key));
    }
}
