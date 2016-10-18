/*
 * Copyright 2013-2016 Gerrit Grunwald.
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import static eu.hansolo.fx.weather.ApiKeys.DARK_SKY_API_KEY;


/**
 * Created by hansolo on 30.09.16.
 */
public class DarkSky {
    public enum Language {
        ARABIC("ar"),
        AZERBAIJANI("az"),
        BELRUSIAN("be"),
        BOSNIAN("bs"),
        CZECH("cs"),
        GERMAN("de"),
        GREEK("el"),
        ENGLISH("en"), // default
        SPANISH("es"),
        FRENCH("fr"),
        CROATIAN("hr"),
        HUNGARIAN("hu"),
        ITALIAN("it"),
        ICELANDIC("is"),
        CORNISH("kw"),
        NORWEGIAN("nb"),
        DUTCH("nl"),
        POLISH("pl"),
        PORTUGUESE("pt"),
        RUSSIAN("ru"),
        SLOVAK("sk"),
        SERBIAN("sr"),
        SWEDISH("sv"),
        TETUM("tet"),
        TURKISH("tr"),
        UKRAINIAN("uk"),
        IKPAY_ATINLAY("x-pig-latin"),
        SIMPLIFIED_CHINESE("zh"),
        TRADITIONAL_CHINESE("zw-tw");

        public String value;

        Language(final String VALUE) {
            value = VALUE;
        }
    }
    public enum Unit {
        US("us"), // imperial units
        SI("si"), // SI units
        CA("ca"), // same as SI except wind speed is in kph
        UK("uk"),
        UK2("uk2"), // same as SI except that nearest storm distance and visibility are in miles and wind speed is in mph
        AUTO("auto"); // units based on geographic location

        public String value;

        Unit(final String VALUE) {
            value = VALUE;
        }
    }
    public enum Condition {
        // Weather Conditions
        NONE("none", "unknown", 0.305085,0.576271),
        CLEAR_DAY("clear-day", "sun", 0.881356, 0.881356),
        CLEAR_NIGHT("clear-night", "sun", 0.881356, 0.881356),
        RAIN("rain", "rain", 0.89831, 0.79661),
        SNOW("snow", "snow", 0.898305, 0.728814),
        SLEET("sleet", "sleet", 0.898305, 0.677966),
        WIND("wind", "wind", 0.813559, 0.474576),
        FOG("fog", "fog", 0.949153, 0.661017),
        CLOUDY("cloudy", "cloud", 0.898305, 0.559322),
        PARTLY_CLOUDY_DAY("partly-cloudy-day", "sun-and-cloud", 0.983051, 0.745763),
        PARTLY_CLOUDY_NIGHT("partly-cloudy-night", "sun-and-cloud", 0.983051, 0.745763),
        HAIL("hail", "sleet", 0.898305, 0.677966),
        THUNDERSTORM("thunderstorm", "thunderstorm", 0.898305, 0.728814),
        // Unit Icons
        PRESSURE("pressure", "pressure", 0.610169, 0.610169),
        HUMIDITY("humidity", "humidity", 0.644068, 0.728814),
        WIND_SPEED("windSpeed", "wind", 0.813559, 0.474576),
        SUNRISE("sunrise", "sun-rise", 1.0, 0.69230769),
        SUNSET("sunset", "sun-set", 1.0, 0.73076923);

        public String value;
        public String styleClass;
        public double widthFactor;
        public double heightFactor;

        Condition(final String VALUE, final String STYLE_CLASS, final double WIDTH_FACTOR, final double HEIGHT_FACTOR) {
            value        = VALUE;
            styleClass   = STYLE_CLASS;
            widthFactor  = WIDTH_FACTOR;
            heightFactor = HEIGHT_FACTOR;
        }
    }
    public enum PrecipType {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow"),
        SLEET("sleet");

        public String value;

        PrecipType(final String VALUE) {
            value = VALUE;
        }
    }
    public enum Exclude {
        CURRENTLY("currently"),
        MINUTELY("minutely"),
        HOURLY("hourly"),
        DAILY("daily"),
        ALERTS("alerts"),
        FLAGS("flags");

        public String value;

        Exclude(final String VALUE) {
            value = VALUE;
        }
    }

    private final  String          DARK_SKY_URL = String.join("", "https://api.darksky.net/forecast/", DARK_SKY_API_KEY.get(), "/");
    private static double          latitude;
    private static double          longitude;
    private static Language        language;
    private static Unit            unit;
    private        TimeZone        timeZone;
    private        DataPoint       today;
    private        List<DataPoint> forecast;
    private        List<Alert>     alerts;
    private        Instant         lastUpdate;


    // ******************** Constructors **************************************
    public DarkSky() {
        this(0, 0);
    }
    public DarkSky(final double LATITUDE, final double LONGITUDE) {
        latitude   = LATITUDE;
        longitude  = LONGITUDE;
        language   = Language.ENGLISH;
        unit       = Unit.CA;
        timeZone   = TimeZone.getDefault();
        today      = new DataPoint();
        forecast   = new LinkedList<>();
        alerts     = new LinkedList<>();
        lastUpdate = Instant.MIN;
    }


    // ******************** Methods *******************************************
    public double getLatitude() { return latitude; }
    public void setLatitude(final double LATITUDE) { latitude = LATITUDE; }

    public double getLongitude() { return longitude; }
    public void setLongitude(final double LONGITUDE) { longitude = LONGITUDE; }

    public void setLatLon(final double LATITUDE, final double LONGITUDE) {
        latitude  = LATITUDE;
        longitude = LONGITUDE;
    }

    public Language getLanguage() { return language; }
    public void setLanguage(final Language LANGUAGE) { language = LANGUAGE; }

    public Unit getUnit() { return unit; }
    public void setUnit(final Unit UNIT) { unit = UNIT; }

    public TimeZone getTimeZone() { return timeZone; }

    public DataPoint getToday() { return today; }

    public List<DataPoint> getForecast() { return forecast; }

    public List<Alert> getAlerts() { return alerts; }

    public boolean update() {
        return update(latitude, longitude);
    }
    public boolean update(final double LATITUDE, final double LONGITUDE) {
        // Update only if lastUpdate is older than 10 min
        if (Instant.now().minusSeconds(600).isBefore(lastUpdate)) return true;

        StringBuilder response = new StringBuilder();
        try {
            forecast.clear();
            alerts.clear();

            final String             URL_STRING = createUrl(LATITUDE, LONGITUDE, unit, language, Exclude.HOURLY, Exclude.MINUTELY, Exclude.FLAGS);
            final HttpsURLConnection CONNECTION = (HttpsURLConnection) new URL(URL_STRING).openConnection();
            final BufferedReader     IN         = new BufferedReader(new InputStreamReader(CONNECTION.getInputStream()));
            String                   inputLine;
            while ((inputLine = IN.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            IN.close();

            Object     obj     = JSONValue.parse(response.toString());
            JSONObject jsonObj = (JSONObject) obj;

            latitude  = Double.parseDouble(jsonObj.getOrDefault("latitude", 0).toString());
            longitude = Double.parseDouble(jsonObj.getOrDefault("longitude", 0).toString());
            timeZone  = TimeZone.getTimeZone(jsonObj.getOrDefault("timezone", "").toString());

            // Update today data
            JSONObject currently = (JSONObject) jsonObj.get("currently");
            setDataPoint(today, currently);

            // Update forecast data
            JSONObject daily = (JSONObject) jsonObj.get("daily");
            JSONArray  days  = (JSONArray) daily.get("data");

            // Update today with more data
            JSONObject day0 = (JSONObject) days.get(0);
            today.setSunriseTime(epochStringToLocalDateTime(day0.getOrDefault("sunriseTime", 0).toString()));
            today.setSunsetTime(epochStringToLocalDateTime(day0.getOrDefault("sunsetTime", 0).toString()));
            today.setPrecipProbability(Double.parseDouble(day0.getOrDefault("precipProbability", 0).toString()));
            today.setPrecipType(PrecipType.valueOf(day0.getOrDefault("precipType", "none").toString().toUpperCase().replace("-", "_")));

            for (int i = 1 ; i < days.size() ; i++) {
                JSONObject day       = (JSONObject) days.get(i);
                DataPoint  dataPoint = new DataPoint();
                setDataPoint(dataPoint, day);
                forecast.add(dataPoint);
            }

            // Update alert data
            if (jsonObj.containsKey("alerts")) {
                JSONArray alerts = (JSONArray) jsonObj.get("alerts");
                for (Object alertObj : alerts) {
                    JSONObject alertJson = (JSONObject) alertObj;
                    Alert      alert     = new Alert();
                    alert.setTitle(alertJson.get("title").toString());
                    alert.setDescription(alertJson.get("description").toString());
                    alert.setTime(epochStringToLocalDateTime(alertJson.getOrDefault("time", 0).toString()));
                    alert.setExpires(epochStringToLocalDateTime(alertJson.getOrDefault("expires", 0).toString()));
                    alerts.add(alert);
                }
            }

            lastUpdate = Instant.now();

            return true;
        } catch (IOException ex) {
            System.out.println(ex);
            return false;
        }
    }

    private void setDataPoint(final DataPoint DATA_POINT, final JSONObject JSON_OBJ) {
        DATA_POINT.setTime(epochStringToLocalDateTime(JSON_OBJ.getOrDefault("time", 0).toString()));
        DATA_POINT.setSummary(JSON_OBJ.getOrDefault("summary", "").toString());
        DATA_POINT.setCondition(Condition.valueOf(JSON_OBJ.getOrDefault("icon", "none").toString().toUpperCase().replace("-", "_")));
        DATA_POINT.setSunriseTime(epochStringToLocalDateTime(JSON_OBJ.getOrDefault("sunriseTime", 0).toString()));
        DATA_POINT.setSunsetTime(epochStringToLocalDateTime(JSON_OBJ.getOrDefault("sunsetTime", 0).toString()));
        DATA_POINT.setMoonPhase(Double.parseDouble(JSON_OBJ.getOrDefault("moonPhase", 0).toString()));
        DATA_POINT.setPrecipIntensity(Double.parseDouble(JSON_OBJ.getOrDefault("precipIntensity", 0).toString()));
        DATA_POINT.setPrecipIntensityMax(Double.parseDouble(JSON_OBJ.getOrDefault("precipIntensityMax", 0).toString()));
        DATA_POINT.setPrecipIntensityMaxTime(epochStringToLocalDateTime(JSON_OBJ.getOrDefault("precipIntensityMaxTime", 0).toString()));
        DATA_POINT.setPrecipProbability(Double.parseDouble(JSON_OBJ.getOrDefault("precipProbability", 0).toString()));
        DATA_POINT.setPrecipType(PrecipType.valueOf(JSON_OBJ.getOrDefault("precipType", "none").toString().toUpperCase().replace("-", "_")));
        DATA_POINT.setTemperature(Double.parseDouble(JSON_OBJ.getOrDefault("temperature", 0).toString()));
        DATA_POINT.setTemperatureMin(Double.parseDouble(JSON_OBJ.getOrDefault("temperatureMin", 0).toString()));
        DATA_POINT.setTemperatureMinTime(epochStringToLocalDateTime(JSON_OBJ.getOrDefault("temperatureMinTime", 0).toString()));
        DATA_POINT.setTemperatureMax(Double.parseDouble(JSON_OBJ.getOrDefault("temperatureMax", 0).toString()));
        DATA_POINT.setTemperatureMaxTime(epochStringToLocalDateTime(JSON_OBJ.getOrDefault("temperatureMaxTime", 0).toString()));
        DATA_POINT.setApparentTemperatureMin(Double.parseDouble(JSON_OBJ.getOrDefault("apparentTemperatureMin", 0).toString()));
        DATA_POINT.setApparentTemperatureMinTime(epochStringToLocalDateTime(JSON_OBJ.getOrDefault("apparentTemperatureMinTime", 0).toString()));
        DATA_POINT.setApparentTemperatureMax(Double.parseDouble(JSON_OBJ.getOrDefault("apparentTemperatureMax", 0).toString()));
        DATA_POINT.setApparentTemperatureMaxTime(epochStringToLocalDateTime(JSON_OBJ.getOrDefault("apparentTemperatureMaxTime", 0).toString()));
        DATA_POINT.setDewPoint(Double.parseDouble(JSON_OBJ.getOrDefault("dewPoint", 0).toString()));
        DATA_POINT.setHumidity(Double.parseDouble(JSON_OBJ.getOrDefault("humidity", 0).toString()));
        DATA_POINT.setWindSpeed(Double.parseDouble(JSON_OBJ.getOrDefault("windSpeed", 0).toString()));
        DATA_POINT.setWindBearing(Double.parseDouble(JSON_OBJ.getOrDefault("windBearing", 0).toString()));
        DATA_POINT.setCloudCover(Double.parseDouble(JSON_OBJ.getOrDefault("cloudCover", 0).toString()));
        DATA_POINT.setPressure(Double.parseDouble(JSON_OBJ.getOrDefault("pressure", 0).toString()));
        DATA_POINT.setOzone(Double.parseDouble(JSON_OBJ.getOrDefault("ozone", 0).toString()));
        DATA_POINT.setNearestStormBearing(Double.parseDouble(JSON_OBJ.getOrDefault("nearestStormBearing", 0).toString()));
        DATA_POINT.setNearestStormDistance(Double.parseDouble(JSON_OBJ.getOrDefault("nearestStormDistance", 0).toString()));
        DATA_POINT.setPrecipAccumlation(Double.parseDouble(JSON_OBJ.getOrDefault("precipAccumlation", 0).toString()));
        DATA_POINT.setVisibility(Double.parseDouble(JSON_OBJ.getOrDefault("visibility", 0).toString()));
    }

    private LocalDateTime epochToLocalDateTime(final long TIMESTAMP) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(TIMESTAMP), ZoneId.of(timeZone.getID()));
    }
    private LocalDateTime epochStringToLocalDateTime(final String TIME_STRING) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(TIME_STRING)), ZoneId.of(timeZone.getID()));
    }
    
    private String createUrl(final double LATITUDE, final double LONGITUDE, final Unit UNIT, final Language LANGUAGE, final Exclude... EXCLUDES) {
        final StringBuilder PARAMETERS = new StringBuilder().append(Double.toString(LATITUDE))
                                                     .append(",")
                                                     .append(Double.toString(LONGITUDE))
                                                     .append("?")
                                                     .append("units=")
                                                     .append(UNIT.value)
                                                     .append("&")
                                                     .append("lang=")
                                                     .append(LANGUAGE.value);
        // Add excludes
        if (EXCLUDES.length > 0) {
            PARAMETERS.append("&").append("exclude=");
            for (Exclude exclude : EXCLUDES) { PARAMETERS.append(exclude.value).append(","); }
            PARAMETERS.deleteCharAt(PARAMETERS.length() - 1);
        }
        final String URL_STRING = String.join("", DARK_SKY_URL, PARAMETERS.toString());
        return URL_STRING;
    }
}
