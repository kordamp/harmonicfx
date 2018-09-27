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
package eu.hansolo.fx.trafficlight;

import javafx.scene.shape.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by hansolo on 02.12.14.
 */
public enum SVGPathParser {
    INSTANCE;

    private static final String  NUM = "(-?\\d*\\.?-?\\d*(?:\\s|,)*)";
    private static final Pattern P   = Pattern.compile(String.join("", "(\\w?)\\s?", NUM, NUM, NUM, NUM, NUM, NUM));
    private static final Matcher M   = P.matcher("");

    public Path processPath(final FillRule FILL_RULE, final String PATH_STRING) {
        final Path PATH = new Path();
        PATH.setFillRule(FILL_RULE);
        M.reset(PATH_STRING);
        while (M.find()) {
            switch (M.group(1).toUpperCase()) {
                case "M":
                    PATH.getElements().add(new MoveTo(Double.parseDouble(M.group(2).replace(",", "")),
                                                      Double.parseDouble(M.group(3))));
                    break;
                case "L":
                    PATH.getElements().add(new LineTo(Double.parseDouble(M.group(2).replace(",", "")),
                                                      Double.parseDouble(M.group(3))));
                    break;
                case "H":
                    PATH.getElements().add(new HLineTo(Double.parseDouble(M.group(2).replace(",", ""))));
                    break;
                case "V":
                    PATH.getElements().add(new VLineTo(Double.parseDouble(M.group(2).replace(",", ""))));
                    break;
                case "C":
                    PATH.getElements().add(new CubicCurveTo(Double.parseDouble(M.group(2).replace(",", "")),
                                                            Double.parseDouble(M.group(3).replace(",", "")),
                                                            Double.parseDouble(M.group(4).replace(",", "")),
                                                            Double.parseDouble(M.group(5).replace(",", "")),
                                                            Double.parseDouble(M.group(6).replace(",", "")),
                                                            Double.parseDouble(M.group(7))));
                    break;
                case "Q":
                    PATH.getElements().add(new QuadCurveTo(Double.parseDouble(M.group(2).replace(",", "")),
                                                           Double.parseDouble(M.group(3).replace(",", "")),
                                                           Double.parseDouble(M.group(4).replace(",", "")),
                                                           Double.parseDouble(M.group(5).replace(",",""))));
                    break;
                case "Z":
                    PATH.getElements().add(new ClosePath());
                    break;
            }
        }
        return PATH;
    }
}
