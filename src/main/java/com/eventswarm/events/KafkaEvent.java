/**
 * Copyright 2020 Andrew Berry 
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
package com.eventswarm.events;

/**
 * Event interface for Kafka events allowing us to retrieve Kafka metadata, normally combined with a payload event type (e.g. JsonEvent).
 *
 * Essentially wraps the methods from ConsumerRecord
 */
public interface KafkaEvent<K,V> extends Event {

    /** default part name for the KafkaPart of a KafkaEvent */
    public static String KAFKA_PART_NAME="KAFKA";
    
    public String topic();
    public int partition();
    public long offset();
    public long timestamp();
    public K key();
    public V value();
}
