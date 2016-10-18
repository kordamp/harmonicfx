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
package eu.hansolo.fx.weather;

import javafx.geometry.Point2D;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import static eu.hansolo.fx.weather.ApiKeys.MAPQUEST_API_KEY;


/**
 * Created by hansolo on 04.10.16.
 */
public class GeoCode {

    private static final String INVERSE_GEO_CODE_URL = String.join("", "https://www.mapquestapi.com/geocoding/v1/reverse?key=", MAPQUEST_API_KEY.get(), "&location=");
    private static final String GEO_CODE_URL         = String.join("", "https://www.mapquestapi.com/geocoding/v1/address?key=", MAPQUEST_API_KEY.get(), "&inFormat=kvp&outFormat=json&location=");

    /**
     * Returns name of City for given latitude and longitude
     * @param LATITUDE
     * @param LONGITUDE
     * @return name of City for given latitude and longitude
     */
    public static String inverseGeoCode(final double LATITUDE, final double LONGITUDE) {
        String URL_STRING = new StringBuilder(INVERSE_GEO_CODE_URL).append(LATITUDE)
                                                                   .append(",")
                                                                   .append(LONGITUDE)
                                                                   .append("&outFormat=json&thumbMaps=false")
                                                                   .toString();

        StringBuilder response = new StringBuilder();
        try {
            final HttpsURLConnection CONNECTION = (HttpsURLConnection) new URL(URL_STRING).openConnection();
            final BufferedReader     IN         = new BufferedReader(new InputStreamReader(CONNECTION.getInputStream()));
            String                   inputLine;
            while ((inputLine = IN.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            IN.close();

            Object     obj           = JSONValue.parse(response.toString());
            JSONObject jsonObj       = (JSONObject) obj;

            JSONArray  results       = (JSONArray) jsonObj.get("results");
            JSONObject firstResult   = (JSONObject) results.get(0);
            JSONArray  locations     = (JSONArray) firstResult.get("locations");
            JSONObject firstLocation = (JSONObject) locations.get(0);
            return firstLocation.get("adminArea5").toString();
        } catch (IOException ex) {
            System.out.println(ex);
            return "";
        }
    }

    /**
     * Returns a JavaFX Point2D object that contains latitude (y) and longitude(x) of the given
     * Address.
     * Example format for STREET_CITY_COUNTRY: "1060 W. Addison St., Chicago IL, 60613"
     * @param STREET_CITY_COUNTRY
     * @return a JavaFX Point2D object that contains latitude(y) and longitude(x) of given address
     */
    public static Point2D geoCode(final String STREET_CITY_COUNTRY) throws UnsupportedEncodingException {
        String URL_STRING = new StringBuilder(GEO_CODE_URL).append(URLEncoder.encode(STREET_CITY_COUNTRY, "UTF-8"))
                                                                   .append("&thumbMaps=false")
                                                                   .toString();

        StringBuilder response = new StringBuilder();
        try {
            final HttpsURLConnection CONNECTION = (HttpsURLConnection) new URL(URL_STRING).openConnection();
            final BufferedReader     IN         = new BufferedReader(new InputStreamReader(CONNECTION.getInputStream()));
            String                   inputLine;
            while ((inputLine = IN.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            IN.close();

            Object     obj           = JSONValue.parse(response.toString());
            JSONObject jsonObj       = (JSONObject) obj;

            JSONArray  results       = (JSONArray) jsonObj.get("results");
            JSONObject firstResult   = (JSONObject) results.get(0);
            JSONArray  locations     = (JSONArray) firstResult.get("locations");
            JSONObject firstLocation = (JSONObject) locations.get(0);
            JSONObject latLng        = (JSONObject) firstLocation.get("latLng");
            return new Point2D(Double.parseDouble(latLng.get("lng").toString()), Double.parseDouble(latLng.get("lat").toString()));
        } catch (IOException ex) {
            System.out.println(ex);
            return new Point2D(0, 0);
        }
    }
}
