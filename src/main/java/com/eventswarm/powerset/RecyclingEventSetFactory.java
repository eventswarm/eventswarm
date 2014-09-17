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
package com.eventswarm.powerset;

import com.eventswarm.eventset.EventSet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * EventSet factory that recycles EventSets that have been pruned by the powerset
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class RecyclingEventSetFactory<KeyType> implements EventSetFactory<KeyType>, EventSetRecycler {
    private Deque<EventSet> available;
    private ReentrantReadWriteLock lock;
    private int max;

    /**
     * Create a recycling eventset factory with a maximum number of eventsets held in the recycling pool.
     *
     * @param max
     */
    public RecyclingEventSetFactory(int max) {
        super();
        this.available = new ArrayDeque<EventSet>(max);
        this.lock = new ReentrantReadWriteLock();
        this.max = max;
    }

    @Override
    public EventSet createEventSet(Powerset<KeyType> pset, KeyType key) {
        try {
            lock.writeLock().lock();
            if (available.isEmpty()) {
                return new EventSet();
            } else {
                return available.pop();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void recycle(EventSet es) {
        es.reset();
        try {
            lock.writeLock().lock();
            if (available.size() < this.max) {
                available.push(es);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
