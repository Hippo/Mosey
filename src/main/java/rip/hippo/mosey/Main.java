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

import rip.hippo.mosey.asm.ClassHierarchy;
import rip.hippo.mosey.configuration.Configuration;
import rip.hippo.mosey.configuration.impl.JavaScriptConfiguration;
import org.tinylog.Logger;


/**
 * @author Hippo
 * @version 1.0.0, 6/23/20
 * @since 1.0.0
 */
public enum Main {
    ;



    public static void main(String[] args) {
        try {
            Configuration configuration = new JavaScriptConfiguration("Config.js"); //TODO: automatically infer the configuration type and implement them and allow user specified conifg path
            Mosey obfuscator = new Mosey(configuration);

            obfuscator.loadRuntime();
            obfuscator.loadInput();
            ClassHierarchy.registerAncestors();

            obfuscator.transform();

            obfuscator.exportJar();
        }catch (Exception e) {
            Logger.error(e, "An error has occurred in HippoObf, please report to Hippo.");
        }
    }
}