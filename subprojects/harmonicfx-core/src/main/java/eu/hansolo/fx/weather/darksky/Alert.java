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
package eu.hansolo.fx.weather.darksky;

import java.time.LocalDateTime;


/**
 * Created by hansolo on 04.10.16.
 */
public class Alert {
    private String        title;
    private String        description;
    private LocalDateTime time;
    private LocalDateTime expires;


    // ******************** Constructors **************************************
    public Alert() {
        title       = "";
        description = "";
        time        = LocalDateTime.MIN;
        expires     = LocalDateTime.MIN;
    }


    // ******************** Methods *******************************************
    public String getTitle() { return title; }
    public void setTitle(final String TITLE) { title = TITLE; }

    public String getDescription() { return description; }
    public void setDescription(final String DESCRIPTION) { description = DESCRIPTION; }

    public LocalDateTime getTime() { return time; }
    public void setTime(final LocalDateTime TIME) { time = TIME; }

    public LocalDateTime getExpires() { return expires; }
    public void setExpires(final LocalDateTime EXPIRES) { expires = EXPIRES; }
}
