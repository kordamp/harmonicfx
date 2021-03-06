/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2013-2020 The original authors
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
package eu.hansolo.fx.gradientpicker.event;

import eu.hansolo.fx.gradientpicker.Handle;
import java.util.EventObject;


public class HandleEvent extends EventObject {
    private final HandleEventType TYPE;


    // ******************** Constructors **************************************
    public HandleEvent(final Object SRC, final HandleEventType TYPE) {
        super(SRC);
        this.TYPE   = TYPE;
    }


    // ******************** Methods *******************************************
    public HandleEventType getType() { return TYPE; }

    public Handle getHandle() { return (Handle) getSource(); }
}
