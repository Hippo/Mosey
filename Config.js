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

//TODO: Make documentation for this

var input = "jars/ElseIfChainBool.jar";
var output = "jars/ElseIfChainBool-obf.jar";
var runtime = "detect";
var dictionary = "AlphaNumeric";

var transformers = [
    "ConfusingSwitch"
];

var libraries = [

]

var FakeTryCatches = {
  chance: 80
};

var FakeJump = {
  chance: 90
};

var ConfusingSwitch = {
    constants: true, chance: 80
}