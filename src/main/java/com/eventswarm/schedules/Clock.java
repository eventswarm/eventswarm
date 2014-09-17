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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eventswarm.schedules;

/**
 * Interface for any class implementing a clock.
 *
 * We're going to assume millisecond precision for now.
 *
 * @author andyb
 */
import java.util.Date;

public interface Clock {

    /**
     * The EPOCH date is when the clock was exactly zero.  See java.util.Date
     * for details.
     * 
     */
    public static Date EPOCH = new Date(0);

    /**
     * The SUNSET date is when the clock has the maximum long value.
     */
    public static Date SUNSET = new Date(Long.MAX_VALUE);
    
    /**
     * Returns true if this clock has been initialised, false otherwise.
     * 
     * @return
     */
    public boolean isInitialised();

    /**
     * Return a java.util.Date class indicating the current time
     *
     * @return
     */
    public Date getTime();

    /** 
     * Return the identity of the clock source.  Note that time from clocks
     * having distinct sources is not directly comparable.
     * 
     * @return
     */
    public Object getSource();

}
