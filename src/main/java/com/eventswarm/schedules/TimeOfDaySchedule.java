/**
 * Copyright 2007-2014 Ensift Pty Ltd as trustee for the Avaz Trust and other contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.eventswarm.schedules;

import java.util.*;

/**
 * Schedule actions to occur at a particular time each day, initialised using the system clock.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class TimeOfDaySchedule extends Schedule {

    private int hour;
    private int minute;
    private int second;
    private Locale locale;
    private GregorianCalendar calendar;

    public TimeOfDaySchedule(int hour) {
        initialise(hour, 0, 0, Locale.getDefault());
    }

    public TimeOfDaySchedule(int hour, int minute) {
        initialise(hour, minute, 0, Locale.getDefault());
    }

    public TimeOfDaySchedule(int hour, int minute, int second, Locale locale) {
        initialise(hour, minute, second, locale);
    }

    private void initialise(int hour, int minute, int second, Locale locale) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.locale = locale;
        this.calendar = new GregorianCalendar(locale);
        this.setTime(new Date());
    }

    public int getHour() {
        return hour;
    }

    private void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    private void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    private void setSecond(int second) {
        this.second = second;
    }

    public Locale getLocale() {
        return locale;
    }

    private void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Set the next time on the current day if we haven't passed the specified time of day, otherwise set the
     * next time to tomorrow at the specified time of day.
     *
     * Note that this class implements only second precision: milliseconds are ignored.
     *
     * @param currentTime
     */
    @Override
    public void setTime(Date currentTime) {
        calendar.setTime(currentTime);
        if (calendar.get(Calendar.HOUR_OF_DAY) > this.hour ||
            calendar.get(Calendar.HOUR_OF_DAY) == this.hour && calendar.get(Calendar.MINUTE) > this.minute ||
            calendar.get(Calendar.HOUR_OF_DAY) == this.hour && calendar.get(Calendar.MINUTE) == this.minute && calendar.get(Calendar.SECOND) >= this.second)
        {
            // roll to tomorrow if the current time is already past the time of day
            calendar.roll(Calendar.DAY_OF_YEAR, true);
        }
        calendar.set(Calendar.HOUR_OF_DAY, this.hour);
        calendar.set(Calendar.MINUTE, this.minute);
        calendar.set(Calendar.SECOND, this.second);
        next = calendar.getTime();
    }
}
