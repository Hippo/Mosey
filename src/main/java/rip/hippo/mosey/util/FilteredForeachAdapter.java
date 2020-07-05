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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Hippo
 * @version 1.0.0, 7/1/20
 * @since 1.0.0
 */
public final class FilteredForeachAdapter<T> {

    private final List<T> elements;
    private final List<Predicate<T>> filters;

    public FilteredForeachAdapter(List<T> elements) {
        this.elements = elements;
        this.filters = new LinkedList<>();
    }

    public FilteredForeachAdapter<T> forEach(Consumer<T> action) {
        for (T element : elements) {
            if (passesFilters(element)) {
                action.accept(element);
            }
        }
        return this;
    }

    public FilteredForeachAdapter<T> addFilter(Predicate<T> filter) {
        filters.add(filter);
        return this;
    }

    public FilteredForeachAdapter<T> clearFilters() {
        filters.clear();
        return this;
    }

    public List<T> toList() {
        return elements;
    }

    private boolean passesFilters(T element) {
        for (Predicate<T> filter : filters) {
            if (!filter.test(element)) {
                return false;
            }
        }
        return true;
    }
}
