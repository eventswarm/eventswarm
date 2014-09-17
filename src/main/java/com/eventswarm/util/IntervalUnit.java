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
package com.eventswarm.util;

/**
 * Supported IntervalUnits for expressing time windowss
 */
public enum IntervalUnit {

    SECONDS, MINUTES, HOURS, DAYS, WEEKS;
    
    public static final long MILLISPERSECOND = 1000;
    public static final long SECSPERSECOND = 1;
    public static final long SECSPERMINUTE = 60;
    public static final long MINUTESPERHOUR = 60;
    public static final long HOURSPERDAY = 24;
    public static final long DAYSPERWEEK = 7;
    public static final long MILLISPERMINUTE = SECSPERMINUTE * MILLISPERSECOND;
    public static final long MILLISPERHOUR = MINUTESPERHOUR * MILLISPERMINUTE;
    public static final long MILLISPERDAY = HOURSPERDAY * MILLISPERHOUR;
    public static final long MILLISPERWEEK = DAYSPERWEEK * MILLISPERDAY;
    public static final long[] MILLISMULTIPLIER = {MILLISPERSECOND, MILLISPERMINUTE, MILLISPERHOUR, MILLISPERDAY, MILLISPERWEEK};
    public static final long SECSPERHOUR = SECSPERMINUTE * MINUTESPERHOUR;
    public static final long SECSPERDAY = SECSPERHOUR * HOURSPERDAY;
    public static final long SECSPERWEEK = SECSPERDAY * DAYSPERWEEK;
    public static final long[] SECSMULTIPLIER = {SECSPERSECOND, SECSPERMINUTE, SECSPERHOUR, SECSPERDAY, SECSPERWEEK};

    public static final String[] shortDisplay = {"sec", "min", "hr", "day", "wk"};

    public Long getIntervalSeconds(Long interval) {
        return interval * SECSMULTIPLIER[this.ordinal()];
    }

    public Long getIntervalMillis(Long interval) {
        return interval * MILLISMULTIPLIER[this.ordinal()];
    }

    /**
     * Override toString() to return a short display name for the units
     * 
     * @return
     */
    @Override
    public String toString() {
        return shortDisplay[this.ordinal()];
    }
}
