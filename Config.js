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

// Set input path to jar
var input = "jars/ElseIfChainBool.jar";

// Set export path
var output = "jars/ElseIfChainBool-obf.jar";

// Leave empty if you have JAVA_HOME environment variable set, if not then manually set it to rt.jar
var runtime = "detect";

// Select a dictionary
/**
 * Dictionary Types:
 * - AlphaNumeric (a-Z | 0-9)
 */
var dictionary = "AlphaNumeric";

// Weather to inline JSR instructions, if you don't know what that is just leave it be
var inlineJSR = true

// Weather to log information about loading/exporting libraries
var logLibraries = false

//Select a list of transformers
/**
 * Note: Visit the transformer class itself to get more information about a specific transformer.
 * Transformers:
 * - BadAnnotation
 * - SyntheticBridge (hides code)
 * - ReverseJump
 * - FakeTryCatches
 * - FakeJump
 * - ConfusingSwitch
 * - JumpRange
 * - ClassEntryHider (hides class zip entries)
 * - BadAttribute
 * - StringEncryption
 */
var transformers = [
    "StringEncryption"
];

// Add the path to all the library jars your jar depends on
var libraries = [

]

// Add any classes you would like to exclude (not obfuscate), note that this is case sensitive
// Adding "org" here would exclude any class beginning with "org" (including package names, eg org.someone.lib.LibClass)
var exclude = [
]


var FakeTryCatches = {
    // The chance it will wrap around an instruction
  chance: 80
};

var FakeJump = {
    // The chance it will insert a jump
  chance: 90
};

var ConfusingSwitch = {
    // If it will confuse constants
    constants: true,
    // The chance the switch will be inserted
    chance: 80
}

var BadAttribute = {
    // If to use bad annotation default attributes
    annotation: true,
    // If to use bad code attributes
    code: true,
    // If to use bad module attributes
    module: true,
    // If to use bad nest host attributes
    nest: true
}

var StringEncryption = {
    intensity: "light"
}