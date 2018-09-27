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
package eu.hansolo.fx.weather;

import eu.hansolo.fx.weather.darksky.DarkSky;
import eu.hansolo.fx.weather.darksky.DarkSky.Condition;
import eu.hansolo.fx.weather.darksky.DarkSky.Language;
import eu.hansolo.fx.weather.darksky.DarkSky.PrecipType;
import eu.hansolo.fx.weather.darksky.DarkSky.Unit;
import eu.hansolo.fx.weather.darksky.DataPoint;
import eu.hansolo.fx.weather.event.WeatherEvent;
import eu.hansolo.fx.weather.event.WeatherEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


/**
 * User: hansolo
 * Date: 01.10.16
 * Time: 05:42
 */
public class Location {
    private          DarkSky                    darkSky;
    private          String                     city;
    private          List<WeatherEventListener> eventListenerList;
    private volatile ScheduledFuture<?>         periodicWeatherTask;
    private static   ScheduledExecutorService   periodicWeatherExecutorService;


    // ******************** Constructors **************************************
    public Location() {
        this(0, 0, null);
    }
    public Location(final double LATITUDE, final double LONGITUDE) {
        this(LATITUDE, LONGITUDE, null);
    }
    public Location(final double LATITUDE, final double LONGITUDE, final String CITY) {
        darkSky           = new DarkSky(LATITUDE, LONGITUDE);
        city              = (null == CITY || CITY.isEmpty()) ? GeoCode.inverseGeoCode(LATITUDE, LONGITUDE) : CITY;
        eventListenerList = new CopyOnWriteArrayList<>();
        scheduleWeatherTask();
    }


    // ******************** Methods *******************************************
    public double getLatitude() { return darkSky.getLatitude(); }
    public void setLatitude(final double LATITUDE) { darkSky.setLatitude(LATITUDE); }

    public double getLongitude() { return darkSky.getLongitude(); }
    public void setLongitude(final double LONGITUDE) { darkSky.setLongitude(LONGITUDE); }

    public String getCity() { return city; }
    public void setCity(final String CITY) { city = CITY; }

    public Language getLanguage() { return darkSky.getLanguage(); }
    public void setLanguage(final Language LANGUAGE) { darkSky.setLanguage(LANGUAGE); }

    public Unit getUnit() { return darkSky.getUnit(); }
    public void setUnit(final Unit UNIT) { darkSky.setUnit(UNIT); }

    public Condition getCondition() { return darkSky.getToday().getCondition(); }
    public double getTemperature() { return darkSky.getToday().getTemperature(); }
    public double getHumidity() { return darkSky.getToday().getHumidity(); }
    public double getPressure() { return darkSky.getToday().getPressure(); }
    public double getWindSpeed() { return darkSky.getToday().getWindSpeed(); }
    public LocalDateTime getSunrise() { return darkSky.getToday().getSunriseTime(); }
    public LocalDateTime getSunset() { return darkSky.getToday().getSunsetTime(); }
    public double getPrecipProbability() { return darkSky.getToday().getPrecipProbability(); }
    public PrecipType getPecipType() { return darkSky.getToday().getPrecipType(); }

    public DataPoint getToday() { return darkSky.getToday(); }

    public List<DataPoint> getForecast() { return darkSky.getForecast(); }


    // ******************** EventHandling *************************************
    public void addWeatherEventListener(final WeatherEventListener LISTENER) { eventListenerList.add(LISTENER); }
    public void setOnWeatherEvent(final WeatherEventListener LISTENER) { addWeatherEventListener(LISTENER); }
    public void removeWeatherEventListener(final WeatherEventListener LISTENER) { eventListenerList.remove(LISTENER); }

    public void fireWeatherEvent(final WeatherEvent EVENT) { eventListenerList.forEach(listener -> listener.onWeatherEvent(EVENT)); }


    // ******************** Scheduled tasks ***********************************
    private synchronized static void enableWeatherExecutorService() {
        if (null == periodicWeatherExecutorService) {
            periodicWeatherExecutorService = new ScheduledThreadPoolExecutor(1, getThreadFactory("WeatherUpdate", false));
        }
    }
    private synchronized void scheduleWeatherTask() {
        enableWeatherExecutorService();
        stopTask(periodicWeatherTask);
        periodicWeatherTask = periodicWeatherExecutorService.scheduleAtFixedRate(
            () -> {
                darkSky.update();
                fireWeatherEvent(new WeatherEvent(Location.this));
            }, 1, 900, TimeUnit.SECONDS); // every 15 min
    }


    public static final ThreadFactory getThreadFactory(final String THREAD_NAME, final boolean IS_DAEMON) {
        return runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(IS_DAEMON);
            return thread;
        };
    }

    public static final void stopTask(ScheduledFuture<?> task) {
        if (null == task) return;
        task.cancel(true);
    }
}