/*
 * Copyright 2014-2016 Gerrit Grunwald.
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
package eu.hansolo.fx.dialplate;

import javafx.event.Event;
import javafx.event.EventType;


/**
 * Created by hansolo on 28.06.16.
 */
public class DialEvent extends Event {
    public static final EventType<DialEvent> NUMBER_DIALED = new EventType(ANY, "numberDiales");
    public        final int NUMBER;

    public DialEvent(final EventType<DialEvent> TYPE, final int NUMBER) {
        super(TYPE);
        this.NUMBER = NUMBER;
    }
}