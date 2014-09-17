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
 * Singleton to generate a unique sequence number that is (almost) guaranteed to be greater than any previous sequence
 * number for a timestamp.
 *
 * This implementation resets the sequence number to 0 whenever time moves forward (i.e. the supplied timestamp is
 * greater than the previous maximum timestamp). To deal with out-of-order requests, a second sequence number starting
 * from Integer.MAX_VALUE/2 is maintained and this is used if an older timestamp is provided. This value is reset when
 * Integer.MAX_VALUE is reached, meaning there is a slight possibility of generating an earlier sequence number when a
 * reset occurs.
 *
 * Note that the implication of generating out-of-order sequence numbers is that two events might be incorrectly ordered
 * if they have the same timestamp. They will still be distinguishable.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class Sequencer {

    private long last;
    private int forwardSeqnr, backSeqnr;
    private static Sequencer instance = new Sequencer();
    // Set a maximum on the number of backwards sequence numbers we use before resetting

    private Sequencer() {
        super();
        setAttrs();
    }

    public static Sequencer getInstance() {
        return instance;
    }

    public static void reset() {
        instance.setAttrs();
    }

    private void setAttrs() {
        last = 0;
        forwardSeqnr = 0;
        backSeqnr = Integer.MAX_VALUE/2;
    }

    public synchronized int getNext(long time) {
        if (time > last) {
            // reset forwards seqnr if we have a newer clock value
            last = time;
            forwardSeqnr = 0;
            return forwardSeqnr;
        } else if (time == last) {
            // same clock, so return next seqnr
            return ++forwardSeqnr;
        } else {
            // Oops, have gone backwards, use the backward sequence number but reset if we've exceeded the MAX
            if (backSeqnr == Integer.MAX_VALUE) backSeqnr = Integer.MAX_VALUE/2;
            return backSeqnr++;
        }
    }
}
