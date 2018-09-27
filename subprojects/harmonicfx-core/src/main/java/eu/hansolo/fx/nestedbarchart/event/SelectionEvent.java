/*
 * Copyright 2013-2018 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.hansolo.fx.nestedbarchart.event;

import eu.hansolo.fx.nestedbarchart.Item;
import eu.hansolo.fx.nestedbarchart.series.Series;


public class SelectionEvent<T extends Item> {
    private Series<T> series;
    private T         item;


    // ******************** Constructors **************************************
    public SelectionEvent(final T ITEM) {
        this(null, ITEM);
    }
    public SelectionEvent(final Series<T> SERIES) {
        this(SERIES, null);
    }
    public SelectionEvent(final Series<T> SERIES, final T ITEM) {
        series = SERIES;
        item   = ITEM;
    }


    // ******************** Methods *******************************************
    public Series<T> getSeries() { return series; }

    public T getItem() { return item; }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder().append("{\n")
                                              .append("  \"series\":\"").append(series.getName()).append("\",\n");
        if (null == item) {
            sb.append("  \"sum\"   :").append(series.getSumOfAllItems()).append("\n");
        } else {
            sb.append("  \"item\"  :\"").append(item.getName()).append("\",\n")
              .append("  \"value\" :").append(item.getValue()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
